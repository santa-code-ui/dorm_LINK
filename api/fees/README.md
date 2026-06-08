# /api/fees  —  Fees & Payments

Implemented by `backend/controllers/FeeController.java` → `FeeService` → `FeeDao`.
`pay` records the payment and recomputes fee status (`UNPAID` → `PARTIAL` → `PAID`) in one transaction.

| Method | Path                    | Body                          | Purpose |
|--------|-------------------------|-------------------------------|---------|
| GET    | `/api/fees/mine`        | — (session)                   | Student's fee records |
| GET    | `/api/fees/receipts`    | — (session)                   | Student's payment receipts |
| GET    | `/api/fees/dues`        | —                             | Outstanding dues across students (admin) |
| POST   | `/api/fees/pay`         | `{feeId, amount, method}`     | Pay; returns `{receiptNo}` |
| POST   | `/api/fees/remind/{id}` | —                             | Send a due reminder (admin) |
