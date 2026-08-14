# 📱 15. RESPONSIVE DESIGN & ACCESSIBILITY (A11Y)
**Project**: UniCalculator (Bharat Pro Financial & GST Neumorphic Calculator)

---

## 1. Responsive Window Size Class Adaptation

```mermaid
graph TD
    WindowSizeClass{Window Width & Posture}
    
    WindowSizeClass -->|Compact < 600dp (Standard Phones)| MobileLayout[Single-Column Vertical Layout<br/>40% Display / 60% Keypad Grid]
    WindowSizeClass -->|Medium 600dp - 840dp (Foldables)| FoldableLayout[Adaptive Split Layout<br/>Top Display / Bottom Wide Keypad]
    WindowSizeClass -->|Expanded > 840dp (Tablets & Desktops)| TabletLayout[Dual-Pane Layout<br/>Left Pane: Live Calc & GST | Right Pane: Permanent Tape & Tally]
```

### Tablet Dual-Pane Architecture:
- **Left Pane (50% Width)**: Recessed Neumorphic LCD Well + 5x5 Keypad Matrix.
- **Right Pane (50% Width)**: Live Calculation Audit Tape + Cash Denomination Ledger + Instant WhatsApp Share Preview.

---

## 2. Accessibility (a11y) & TalkBack Standards

1. **Touch Target Size**:
   - Every Neumorphic key has a minimum touch target of **48dp × 48dp** (typically `64dp × 56dp`), exceeding WCAG 2.2 AA standards.
2. **TalkBack Semantic Announcements in English & Indian Languages**:
   - Rather than reading raw characters (`"1, 2, 5, 0, 0, 0"`), Compose semantics announce formal amounts:
     - `contentDescription = "Amount: One Lakh Twenty-Five Thousand Rupees"`
   - Operator semantics:
     - `+18%` key: `"Add Eighteen Percent Goods and Services Tax"`
     - `-18%` key: `"Extract Eighteen Percent Reverse GST from Gross Price"`
3. **High Contrast Color Calibration**:
   - Neumorphism shadow contrasts maintain a minimum text-to-background contrast ratio of **7:1** for primary numerals and **4.5:1** for secondary labels.
