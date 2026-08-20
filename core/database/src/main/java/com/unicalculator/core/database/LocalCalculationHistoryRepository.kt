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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocalCalculationHistoryRepository private constructor(context: Context) {
    private val dbHelper = DatabaseHelper(context.applicationContext)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _historyList = MutableStateFlow<List<CalculationHistoryItem>>(emptyList())
    val historyList: StateFlow<List<CalculationHistoryItem>> = _historyList.asStateFlow()

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
        android.util.Log.d("UniCalcHistory", "Loaded ${items.size} history items from DB: $items")
    }

    fun insert(item: CalculationHistoryItem) {
        android.util.Log.d("UniCalcHistory", "Inserting history item: $item")
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
                val rowId = db.insert(TABLE_NAME, null, values)
                android.util.Log.d("UniCalcHistory", "Inserted rowId: $rowId")
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

    fun deleteByTypes(types: List<CalculationType>) {
        scope.launch {
            withContext(Dispatchers.IO) {
                val db = dbHelper.writableDatabase
                if (types.isEmpty()) {
                    db.delete(TABLE_NAME, null, null)
                } else {
                    val placeholders = types.joinToString(",") { "?" }
                    val args = types.map { it.name }.toTypedArray()
                    db.delete(TABLE_NAME, "$COLUMN_TYPE IN ($placeholders)", args)
                }
            }
            loadAllFromDb()
        }
    }

    fun togglePin(id: Long, currentPinStatus: Boolean) {
        scope.launch {
            withContext(Dispatchers.IO) {
                val db = dbHelper.writableDatabase
                val values = ContentValues().apply {
                    put(COLUMN_PINNED, if (!currentPinStatus) 1 else 0)
                }
                db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
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
            loadAllFromDb()
        }
    }

    companion object {
        private const val DB_NAME = "unicalculator_history.db"
        private const val DB_VERSION = 2
        private const val TABLE_NAME = "calculation_history"

        private const val COLUMN_ID = "id"
        private const val COLUMN_TIMESTAMP = "timestamp"
        private const val COLUMN_TYPE = "calculation_type"
        private const val COLUMN_FORMULA = "formula_expression"
        private const val COLUMN_RESULT = "primary_result"
        private const val COLUMN_NET_BASE = "net_base_amount"
        private const val COLUMN_TOTAL_TAX = "total_tax_amount"
        private const val COLUMN_CGST = "cgst_amount"
        private const val COLUMN_SGST = "sgst_amount"
        private const val COLUMN_IGST = "igst_amount"
        private const val COLUMN_MEMO = "memo_note"
        private const val COLUMN_PINNED = "is_pinned"

        @Volatile
        private var INSTANCE: LocalCalculationHistoryRepository? = null

        fun getInstance(context: Context): LocalCalculationHistoryRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocalCalculationHistoryRepository(context.applicationContext).also { INSTANCE = it }
            }
        }

        operator fun invoke(context: Context): LocalCalculationHistoryRepository = getInstance(context)
    }

    private class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            val sql = """
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
            db.execSQL(sql)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    }
}

private fun Cursor.getStringOrNull(columnIndex: Int): String? {
    return if (isNull(columnIndex)) null else getString(columnIndex)
}
