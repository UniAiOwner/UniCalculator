package com.unicalculator.core.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.unicalculator.core.model.CalculationHistoryItem
import com.unicalculator.core.model.CalculationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocalCalculationHistoryRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context.applicationContext)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _historyList = MutableStateFlow<List<CalculationHistoryItem>>(emptyList())
    val historyList: Flow<List<CalculationHistoryItem>> = _historyList.asStateFlow()

    init {
        scope.launch {
            loadAllFromDb()
        }
    }

    private suspend fun loadAllFromDb() = withContext(Dispatchers.IO) {
        val items = mutableListOf<CalculationHistoryItem>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_TIMESTAMP DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val item = CalculationHistoryItem(
                    id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                    timestamp = it.getLong(it.getColumnIndexOrThrow(COLUMN_TIMESTAMP)),
                    type = try {
                        CalculationType.valueOf(it.getString(it.getColumnIndexOrThrow(COLUMN_TYPE)))
                    } catch (_: Exception) {
                        CalculationType.STANDARD_MATH
                    },
                    formulaExpression = it.getString(it.getColumnIndexOrThrow(COLUMN_FORMULA)),
                    primaryResult = it.getString(it.getColumnIndexOrThrow(COLUMN_RESULT)),
                    netBaseAmount = it.getStringOrNull(it.getColumnIndexOrThrow(COLUMN_NET_BASE)),
                    totalTaxAmount = it.getStringOrNull(it.getColumnIndexOrThrow(COLUMN_TOTAL_TAX)),
                    cgstAmount = it.getStringOrNull(it.getColumnIndexOrThrow(COLUMN_CGST)),
                    sgstAmount = it.getStringOrNull(it.getColumnIndexOrThrow(COLUMN_SGST)),
                    igstAmount = it.getStringOrNull(it.getColumnIndexOrThrow(COLUMN_IGST)),
                    memoNote = it.getStringOrNull(it.getColumnIndexOrThrow(COLUMN_MEMO)),
                    isPinned = it.getInt(it.getColumnIndexOrThrow(COLUMN_PINNED)) == 1
                )
                items.add(item)
            }
        }
        _historyList.value = items
    }

    fun insert(item: CalculationHistoryItem) {
        scope.launch {
            withContext(Dispatchers.IO) {
                val db = dbHelper.writableDatabase
                val values = ContentValues().apply {
                    put(COLUMN_TIMESTAMP, item.timestamp)
                    put(COLUMN_TYPE, item.type.name)
                    put(COLUMN_FORMULA, item.formulaExpression)
                    put(COLUMN_RESULT, item.primaryResult)
                    put(COLUMN_NET_BASE, item.netBaseAmount)
                    put(COLUMN_TOTAL_TAX, item.totalTaxAmount)
                    put(COLUMN_CGST, item.cgstAmount)
                    put(COLUMN_SGST, item.sgstAmount)
                    put(COLUMN_IGST, item.igstAmount)
                    put(COLUMN_MEMO, item.memoNote)
                    put(COLUMN_PINNED, if (item.isPinned) 1 else 0)
                }
                db.insert(TABLE_NAME, null, values)
            }
            loadAllFromDb()
        }
    }

    fun deleteById(id: Long) {
        scope.launch {
            withContext(Dispatchers.IO) {
                val db = dbHelper.writableDatabase
                db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
            }
            loadAllFromDb()
        }
    }

    fun clearAll() {
        scope.launch {
            withContext(Dispatchers.IO) {
                val db = dbHelper.writableDatabase
                db.delete(TABLE_NAME, null, null)
            }
            _historyList.value = emptyList()
        }
    }

    private fun Cursor.getStringOrNull(index: Int): String? {
        return if (isNull(index)) null else getString(index)
    }

    private class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onConfigure(db: SQLiteDatabase) {
            super.onConfigure(db)
            db.enableWriteAheadLogging()
        }

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE $TABLE_NAME (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_TIMESTAMP INTEGER NOT NULL,
                    $COLUMN_TYPE TEXT NOT NULL,
                    $COLUMN_FORMULA TEXT NOT NULL,
                    $COLUMN_RESULT TEXT NOT NULL,
                    $COLUMN_NET_BASE TEXT,
                    $COLUMN_TOTAL_TAX TEXT,
                    $COLUMN_CGST TEXT,
                    $COLUMN_SGST TEXT,
                    $COLUMN_IGST TEXT,
                    $COLUMN_MEMO TEXT,
                    $COLUMN_PINNED INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX idx_calc_timestamp ON $TABLE_NAME($COLUMN_TIMESTAMP DESC)")
            db.execSQL("CREATE INDEX idx_calc_type ON $TABLE_NAME($COLUMN_TYPE)")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    }

    companion object {
        private const val DATABASE_NAME = "unicalculator_history.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "calculation_history"

        private const val COLUMN_ID = "id"
        private const val COLUMN_TIMESTAMP = "timestamp"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_FORMULA = "formula"
        private const val COLUMN_RESULT = "result"
        private const val COLUMN_NET_BASE = "net_base"
        private const val COLUMN_TOTAL_TAX = "total_tax"
        private const val COLUMN_CGST = "cgst"
        private const val COLUMN_SGST = "sgst"
        private const val COLUMN_IGST = "igst"
        private const val COLUMN_MEMO = "memo"
        private const val COLUMN_PINNED = "is_pinned"
    }
}

