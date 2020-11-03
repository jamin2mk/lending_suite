﻿--DROP TABLE "ADM_SEQUENCE_MASTER";

CREATE TABLE "ADM_SEQUENCE_MASTER" 
   (	
   "ID" NUMBER GENERATED ALWAYS AS IDENTITY NOT NULL,
   "SEQUENCE_MASTER_CODE" VARCHAR2(50) DEFAULT ('SEQ1' || to_char(SYSDATE, 'yymmdd') || to_char("ADM_SEQUENCE_SEQ"."NEXTVAL")),
   "MODULE_SIGN" VARCHAR2(4), 
	 "MODULE_NAME" VARCHAR2(100), 
   "CODE" VARCHAR2(3), 
 	 "SUB_CODE" VARCHAR2(1), 
 	 "TABLE_NAME" VARCHAR2(50), 
	 "SEG_NAME" VARCHAR2(50), 
   "CREATE_DATE" DATE DEFAULT SYSDATE,
   CONSTRAINT "ADM_SEQUENCE_MASTER_PK" PRIMARY KEY ("ID"),
	 CONSTRAINT "ADM_SEQUENCE_MASTER_CTR_1" UNIQUE ("CODE", "SUB_CODE"),  
	 CONSTRAINT "ADM_SEQUENCE_MASTER_CTR_2" UNIQUE ("TABLE_NAME")
   );
   COMMENT ON COLUMN "ADM_SEQUENCE_MASTER"."MODULE_SIGN" IS 'ki hieu module';
   COMMENT ON COLUMN "ADM_SEQUENCE_MASTER"."MODULE_NAME" IS 'ten module';
   COMMENT ON COLUMN "ADM_SEQUENCE_MASTER"."CODE" IS 'ma so module';
   COMMENT ON COLUMN "ADM_SEQUENCE_MASTER"."SUB_CODE" IS 'ma con trong module';
   COMMENT ON COLUMN "ADM_SEQUENCE_MASTER"."TABLE_NAME" IS 'ten bang trong module';
   COMMENT ON COLUMN "ADM_SEQUENCE_MASTER"."SEG_NAME" IS 'ten sequence cua oracle de tao so tang';
   COMMENT ON COLUMN "ADM_SEQUENCE_MASTER"."CREATE_DATE" IS 'ngay tao';