``` sql 
-- DELETE ALL DATA NEWEPS


DELETE p FROM PM.Prescription p 
WHERE p.PrescriptionSourceId = 5  AND (p.NoIDEPS = '1' OR  p.NoIDEPS = '900156264') AND (p.NoPrescription IS NULL OR p.NoPrescription = '') AND p.IdFormulaState = 0



DELETE m FROM PM.Medication m
WHERE m.PrescriptionId IN (
SELECT p.Id FROM PM.Prescription p 
WHERE p.PrescriptionSourceId = 5  AND (p.NoIDEPS = '1' OR  p.NoIDEPS = '900156264') AND (p.NoPrescription IS NULL OR p.NoPrescription = '') AND p.IdFormulaState = 0
)

DELETE pd FROM PM.PrescriptionDelivery pd
WHERE pd.PrescriptionId IN (
SELECT p.Id FROM PM.Prescription p 
WHERE p.PrescriptionSourceId = 5  AND (p.NoIDEPS = '1' OR  p.NoIDEPS = '900156264') AND (p.NoPrescription IS NULL OR p.NoPrescription = '') AND p.IdFormulaState = 0
)
```