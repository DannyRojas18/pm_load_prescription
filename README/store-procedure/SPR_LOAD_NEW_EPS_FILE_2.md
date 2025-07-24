``` sql 
CREATE PROCEDURE [PM].[SPR_LOAD_NEW_EPS_FILE_2]
AS
BEGIN TRY

  DECLARE @Preauthorization NVARCHAR(200);
  DECLARE @Metadata NVARCHAR(MAX);
  DECLARE @IdPrescription NVARCHAR(50);
  DECLARE @DocumentId NVARCHAR(50);
  DECLARE @DocumentType NVARCHAR(20);
  DECLARE @DeliveryDate DATETIME;
  DECLARE @CreateDate DATETIME;
  DECLARE @Timesta DATETIME;
  DECLARE @ConsultCursor CURSOR;
  DECLARE @IdPres NVARCHAR(100);

  UPDATE PM.loadneweps
  SET metadata = REPLACE( metadata, '{{ID_AUTOINCREMENT}}', codePreauthorizationNeps ),
      codePreauthorization = codePreauthorizationNeps
  WHERE codePreauthorization IS NULL;

  SET @ConsultCursor = CURSOR FOR SELECT CONCAT(LNE.CodePreauthorization,'-',LNE.CodePreauthorizationNeps ), LNE.Metadata, P.Id, LNE.DocumentId, LNE.DocumentType, LNE.DeliveryDate, LNE.CreateDate, LNE.[TimeStamp]
                                  FROM PM.LoadNewEps LNE WITH ( NOLOCK )
                                       LEFT JOIN PM.Prescription P WITH ( NOLOCK )
                                         ON LNE.codePreauthorization = P.PrescriptionId
                                  WHERE LNE.metadata IS NOT NULL;

  OPEN @ConsultCursor;
  FETCH NEXT FROM @ConsultCursor INTO @Preauthorization, @Metadata, @IdPrescription, @DocumentId, @DocumentType, @DeliveryDate, @CreateDate, @Timesta;
  WHILE @@FETCH_STATUS = 0
  BEGIN
    IF( @IdPrescription IS NOT NULL )
    BEGIN
      -- Update metadata of medication
      UPDATE PM.Medication
      SET MetaData = @Metadata
      WHERE PrescriptionId = @IdPrescription;
    END
    ELSE
    BEGIN
       SET @IdPres = ( SELECT NEWID() );

       -- Prescription is created.
       INSERT INTO PM.Prescription ( Id,
                                     PrescriptionId,
                                     NoPrescription,
                                     PrescriptionSourceId,
                                     NoIdPatient,
                                     TypeIdPatient,
                                     MetaData,
                                     PrescriptionStatusId,
                                     DateMaxDelivery,
                                     CodEPS ,
                                     NoIDEPS,
                                     CreateDate,
                                     [Timestamp],
                                     IdFormulaState )
       VALUES( @IdPres,
               @Preauthorization,
               '',
               5, -- This id is new eps
               @DocumentId,
               @DocumentType,
               '{}',
               0, -- This state is "consulta"
               @DeliveryDate,
               '900156264', -- This code is the nit of new eps
               '1', -- This id is the new eps
               @CreateDate,
               @Timesta,
               0 -- this is is "no entregado"
         );

      INSERT INTO PM.Medication ( Id,
                                  PrescriptionId ,
                                  EpsId,
                                  MetaData ,
                                  [Timestamp] )
      VALUES( NEWID(),
              @IdPres,
              '1', -- Nueva EPS
              @Metadata,
              @Timesta
            );
    END;

    FETCH NEXT FROM @ConsultCursor INTO @Preauthorization, @Metadata, @IdPrescription, @DocumentId, @DocumentType, @DeliveryDate, @CreateDate, @Timesta;
  END

  CLOSE @ConsultCursor;
  DEALLOCATE @ConsultCursor;

  SELECT @@ROWCOUNT
END TRY
BEGIN CATCH
  SELECT ERROR_NUMBER() AS ErrorNumber,
         ERROR_STATE() AS ErrorState,
         ERROR_SEVERITY() AS ErrorSeverity,
         ERROR_PROCEDURE() AS ErrorProcedure,
         ERROR_LINE() AS ErrorLine,
         ERROR_MESSAGE() AS ErrorMessage;
END CATCH
```