CREATE OR REPLACE PACKAGE PROCESS_INSTANCE IS

  -- Author  : TRANGNMT
  -- Created : 31/10/2020 5:46:02 PM
  -- Purpose : 

  -- Public type declarations
  TYPE REFCUR IS REF CURSOR;

  -- Public function and procedure declarations
  PROCEDURE INSERT_BPM_INSTANCES(iInstanceData CLOB, oResult OUT REFCUR);

END PROCESS_INSTANCE;
/
CREATE OR REPLACE PACKAGE BODY PROCESS_INSTANCE IS

  PROCEDURE INSERT_BPM_INSTANCES(iInstanceData CLOB, oResult OUT REFCUR) AS
  
    vjInstanceData JSON;
    vInstanceId    BPM_ROOT_INSTANCE.Instance_Id%TYPE;    
  BEGIN
  
    DBMS_OUTPUT.PUT_LINE('Handle json data'); --logging
  
    vjInstanceData := JSON(iInstanceData);
    vInstanceId    := json_ext.get_string(vjInstanceData, 'piid');
    /*vProcessName   := json_ext.get_string(vjInstanceData, 'processTemplateName');
    vState         := json_ext.get_string(vjInstanceData, 'state');*/
  
    DBMS_OUTPUT.PUT_LINE('Insert into table'); --logging
  
    INSERT INTO BPM_ROOT_INSTANCE
      (instance_id, data)
    VALUES
      (vInstanceId, iInstanceData);
  
    OPEN oResult FOR
      SELECT '-1' AS LOG_ID,
             DBMS_UTILITY.FORMAT_ERROR_BACKTRACE AS LOG_MESSAGE
        FROM DUAL;
    COMMIT;
  
  EXCEPTION
    WHEN OTHERS THEN
      BEGIN
        ROLLBACK;
        OPEN oResult FOR
          SELECT '-1' AS LOG_ID,
                 DBMS_UTILITY.FORMAT_ERROR_BACKTRACE AS LOG_MESSAGE
            FROM DUAL;
      END;
  END INSERT_BPM_INSTANCES;

END PROCESS_INSTANCE;
/
