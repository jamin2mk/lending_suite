﻿--DROP SEQUENCE "ADM_SEQUENCE_SEQ";
CREATE SEQUENCE "ADM_SEQUENCE_SEQ"
       START WITH 1
       INCREMENT BY 1
       MAXVALUE 9999999999999999999999999
       NOCYCLE
       CACHE 20;

--DROP SEQUENCE "BPM_INSTANCE_SEQ";
CREATE SEQUENCE "BPM_INSTANCE_SEQ" 
       START WITH 1
       INCREMENT BY 1
       MAXVALUE 9999999999999999999999999
       NOCYCLE
       CACHE 20;
       
--DROP SEQUENCE "BPM_TASK_SEQ";
CREATE SEQUENCE "BPM_TASK_SEQ" 
       START WITH 1
       INCREMENT BY 1
       MAXVALUE 9999999999999999999999999
       NOCYCLE
       CACHE 20;
       
