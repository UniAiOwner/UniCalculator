# 🔄 08. USER FLOWS & INTERACTION STATE MACHINES
**Project**: UniCalculator (Bharat Pro Financial & GST Neumorphic Calculator)

---

## 1. GST Calculation Flow (Forward & Reverse)

```mermaid
stateDiagram-v2
    [*] --> Idle: App opened / GST Tab selected
    Idle --> TypingAmount: User types digits (e.g., 2,50,000)
    
    state TypingAmount {
        [*] --> FormatVedic: Live format as ₹ 2,50,000
        FormatVedic --> DisplayLCD: Render in Neumorphic LCD Well
    }
    
    TypingAmount --> ForwardSlabSelected: Tap +5%, +12%, +18%, +28%
    TypingAmount --> ReverseSlabSelected: Tap -5%, -12%, -18%, -28%
    TypingAmount --> CustomSlabSelected: Tap "Custom %"
    
    state ForwardSlabSelected {
        [*] --> ExecForwardMath: Base = Input, Tax = Base * Rate / 100
        ExecForwardMath --> ComputeGross: Gross = Base + Tax
    }
    
    state ReverseSlabSelected {
        [*] --> ExecReverseMath: Gross = Input, Base = Gross * 100 / (100 + Rate)
        ExecReverseMath --> ComputeTax: Tax = Gross - Base
    }
    
    ForwardSlabSelected --> CheckJurisdiction
    ReverseSlabSelected --> CheckJurisdiction
    
    state CheckJurisdiction {
        [*] --> IntraState: Intra-State -> CGST = Tax/2, SGST = Tax/2
        [*] --> InterState: Inter-State -> IGST = Tax
    }
    
    CheckJurisdiction --> RenderBreakdownCards: Animate soft Neumorphic Cards
    RenderBreakdownCards --> UserOutputAction
    
    state UserOutputAction {
        [*] --> CopyToClipboard: Tap Copy
        [*] --> ShareWhatsApp: Tap Share Slip
        [*] --> SaveToTape: Auto-logged to History
    }
    
    UserOutputAction --> [*]
```

---

## 2. Cash Denomination Tally (Rokad) Flow

```mermaid
stateDiagram-v2
    [*] --> TallyIdle: Opens Cash Tally Tab
    TallyIdle --> FocusRow: Taps note row (e.g. ₹500)
    
    state FocusRow {
        [*] --> IncrementStepper: Tap '+' or '-'
        [*] --> DirectKeypadInput: Type count (e.g. 145)
    }
    
    FocusRow --> RecalculateTally: On Value Change
    
    state RecalculateTally {
        [*] --> RowSubtotal: RowTotal = Count * Denomination
        RowSubtotal --> GrandTotal: Sum all active rows + Coins
        GrandTotal --> NoteCounter: Sum total physical notes
        NoteCounter --> ConvertWords: Generate English & Hindi Words in Lakhs/Crores
    }
    
    RecalculateTally --> UpdateSummaryHeader: Animate Neumorphic Display Plate
    UpdateSummaryHeader --> ExportOrReset
    
    state ExportOrReset {
        [*] --> WhatsAppShare: Generate clean text summary with timestamp
        [*] --> ResetClear: Clear all counts (with undo toast)
    }
    
    ExportOrReset --> [*]
```
