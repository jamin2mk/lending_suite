﻿--DROP TABLE "BPM_TASK_DETAIL";

CREATE TABLE "BPM_TASK_DETAIL" 
   (	
   "ID" NUMBER GENERATED ALWAYS AS IDENTITY NOT NULL,
   "TASK_DETAIL_CODE" VARCHAR2(50) DEFAULT ('TSK2' || to_char(SYSDATE, 'yymmdd') || to_char("BPM_TASK_SEQ"."NEXTVAL")),
   "TASK_MASTER_CODE" VARCHAR2(50),
   "TASK_ID" VARCHAR2(50),
   "BUSINESS_OBJECT" CLOB,
	 "CREATE_DATE" DATE DEFAULT SYSDATE,
   CONSTRAINT "BPM_TASK_DETAIL_PK" PRIMARY KEY ("ID")
   );
   COMMENT ON COLUMN "BPM_TASK_DETAIL"."TASK_DETAIL_CODE" IS 'Mã quản lý Task detail trên database.';
   COMMENT ON COLUMN "BPM_TASK_DETAIL"."TASK_MASTER_CODE" IS 'Mã quản lý Task master trên database.';
   COMMENT ON COLUMN "BPM_TASK_DETAIL"."TASK_ID" IS 'TaskId trên BPM.';
   COMMENT ON COLUMN "BPM_TASK_DETAIL"."BUSINESS_OBJECT" IS 'Data của Task';
   COMMENT ON COLUMN "BPM_TASK_DETAIL"."CREATE_DATE" IS 'Ngày tạo trên database. Default = SYSDATE';
