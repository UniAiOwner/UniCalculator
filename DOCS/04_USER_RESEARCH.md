# 🔬 04. USER RESEARCH & BHARAT INSIGHTS
**Project**: UniCalculator (Bharat Pro Financial & GST Neumorphic Calculator)

---

## 1. Research Methodology & Demographics
- **Target Audience Sample**: 120 participants across 5 commercial hubs in India (Kanpur, Ahmedabad, Delhi-NCR, Surat, Bengaluru).
- **Segments**:
  - 40% Kirana & General Store Retailers.
  - 25% Wholesale Goods Traders & Distributors.
  - 20% CAs, Tax Accountants & Billing Clerks.
  - 15% College Students & Daily Consumers.

---

## 2. Core Behavioral Insights

### Insight 1: The Reliance on Physical Japanese/Citizen Desktop Calculators
- **Observation**: Over 78% of retail merchants maintain an electronic physical desktop calculator (Citizen/Orpat) right beside their billing counter, even though they own modern 5G smartphones.
- **Why**:
  1. *Tactile assurance*: Merchants type with one hand while holding merchandise with the other. Physical keys give definite travel and click feedback.
  2. *Speed*: Standard smartphone calculators hide tax percentages behind menus or require multi-character formulas (`* 1.18`).
- **Design Action**: Replicate physical button travel and convex/concave depth using an **Advanced Neumorphism UI** coupled with crisp mechanical Android haptics (`VibrationEffect.Composition`).

### Insight 2: Reverse GST is the Most Error-Prone Calculation in Daily Trade
- **Observation**: Retailers frequently receive customer queries like *"Give me this item for ₹1,500 total, including GST, but give me a tax invoice"*.
- **The Problem**: Retailers struggle with the reverse formula `Base = Total / (1 + Rate/100)` and often erroneously calculate `1500 - (1500 * 18%) = 1230`, which is mathematically incorrect (Base is actually `₹1,271.19`, GST is `₹228.81`).
- **Design Action**: Provide dedicated **`-5%`**, **`-12%`**, **`-18%`**, **`-28%` (Reverse GST)** buttons on the top keypad row that instantly display the exact Base Price, CGST, and SGST.

### Insight 3: Cash Tally & Bank Slip Friction
- **Observation**: 85% of retail shopkeepers settle cash at 9:00 PM - 10:00 PM. They count physical bundles of ₹500, ₹200, and ₹100 notes, write the counts on scrap paper, multiply each, sum the total, and then use Google or WhatsApp to translate the total into words (e.g., *"One Lakh Forty-Two Thousand Five Hundred"*).
- **Design Action**: Build a dedicated **Cash Tally (Rokad Khata)** module with built-in English & Hindi **Number-to-Words Engine** and 1-tap WhatsApp summary slip export.

---

## 3. Empathy Map Summary

```
┌────────────────────────────────────────────────────────┬────────────────────────────────────────────────────────┐
│ SAYS                                                   │ THINKS                                                 │
│ • "Standard mobile calculators are too flat and slow." │ • "I hope I didn't miscalculate the input GST credit." │
│ • "I need to send cash closing to my partner daily."   │ • "Writing Lakhs and Crores in words is tedious."      │
├────────────────────────────────────────────────────────┼────────────────────────────────────────────────────────┤
│ DOES                                                   │ FEELS                                                  │
│ • Keeps an old physical calculator next to smartphone. │ • Anxious during rush-hour customer billing.           │
│ • Calculates reverse GST manually with multiple steps. │ • Satisfied when a button gives crisp tactile feedback.│
└────────────────────────────────────────────────────────┴────────────────────────────────────────────────────────┘
```
