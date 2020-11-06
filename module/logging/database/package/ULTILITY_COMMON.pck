CREATE OR REPLACE PACKAGE ULTILITY_COMMON IS

  -- Author  : TRANGNMT
  -- Created : 04/11/2020 5:23:53 PM
  -- Purpose : 

  -- Public function and procedure declarations
  FUNCTION GEN_CODE(iModule VARCHAR2, iTable VARCHAR2) RETURN VARCHAR2;

END ULTILITY_COMMON;
/
CREATE OR REPLACE PACKAGE BODY ULTILITY_COMMON IS

  -- Function and procedure implementations
  FUNCTION GEN_CODE(iModule VARCHAR2, iTable VARCHAR2) RETURN VARCHAR2 IS
    vKey ADM_SEQUENCE.SEQUENCE_CODE%TYPE;
    vCount   NUMBER;
    vSeqName ADM_SEQUENCE.SEG_NAME%TYPE;
    vSeqNo   NUMBER;
  BEGIN
    vKey := '';
  
    SELECT COUNT(*)
      INTO vCount
      FROM ADM_SEQUENCE t
     WHERE t.module_sign = upper(iModule)
       AND t.table_name = upper(iTable);
  
    IF nvl(vCount, 0) > 0 THEN
      SELECT t.code || t.sub_code, t.seg_name
        INTO vKey, vSeqName
        FROM ADM_SEQUENCE t
       WHERE t.module_sign = upper(iModule)
         AND t.table_name = upper(iTable);
    
      EXECUTE IMMEDIATE 'select ' || nvl(vSeqName, 'ADM_SEQUENCE_SEQ') ||
                        '.nextval from dual'
        INTO vSeqNo;
    
      vKey := vKey || to_char(SYSDATE, 'yymmdd') || vSeqNo;
    
    END IF;
  
    RETURN vkey;
  
  EXCEPTION
    WHEN OTHERS THEN
      RETURN NULL;
  END;

END ULTILITY_COMMON;
/
