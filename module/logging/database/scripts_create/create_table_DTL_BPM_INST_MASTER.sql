﻿--DROP TABLE DTL_BPM_INST_MASTER;
       
CREATE TABLE DTL_BPM_INST_MASTER
  (
  "ID" NUMBER GENERATED ALWAYS AS IDENTITY NOT NULL,
  "INST_MASTER_CODE" VARCHAR2(50) DEFAULT ('INS1' || to_char(SYSDATE, 'yymmdd') || to_char("BPM_INSTANCE_SEQ"."NEXTVAL")),
  "INST_ID" VARCHAR2(50) NOT NULL,  
  "STATE" VARCHAR2(20),
  "CREATE_DATE" DATE,
  "DUE_DATE" DATE,
  "MODIFICATION_DATE" DATE,  
  "SNAPSHOT_ID" VARCHAR2(50),
  "BRANCH_ID" VARCHAR2(50),
  "PROCESS_APP_ID" VARCHAR2(50),
  "PROCESS_NAME" VARCHAR2(100),
  "PULL_TIME" DATE DEFAULT SYSDATE,
  "STARTER_ID" VARCHAR (50),
   CONSTRAINT "DTL_BPM_INST_MASTER_PK" PRIMARY KEY ("ID")
   );
   COMMENT ON COLUMN "DTL_BPM_INST_MASTER"."ID" IS 'Khóa chính';
   COMMENT ON COLUMN "DTL_BPM_INST_MASTER"."INST_MASTER_CODE" IS 'Mã quản lý Instance trên database';
   COMMENT ON COLUMN "DTL_BPM_INST_MASTER"."INST_ID" IS 'Mã instance ứng với InstanceID trên BPM';
   COMMENT ON COLUMN "DTL_BPM_INST_MASTER"."STATE" IS 'Trạng thái instance trên BPM: Commpleted, Failed...';
   COMMENT ON COLUMN "DTL_BPM_INST_MASTER"."CREATE_DATE" IS 'Ngày tạo instance trên BPM';
   COMMENT ON COLUMN "DTL_BPM_INST_MASTER"."DUE_DATE" IS 'Thời gian hoàn thành mong đợi';
   COMMENT ON COLUMN "DTL_BPM_INST_MASTER"."MODIFICATION_DATE" IS 'Ngày chỉnh sửa instance trên BPM';
   COMMENT ON COLUMN "DTL_BPM_INST_MASTER"."SNAPSHOT_ID" IS 'SnapshotID của instance trên BPM';
   COMMENT ON COLUMN "DTL_BPM_INST_MASTER"."BRANCH_ID" IS 'BranchID của instance từ BPM';
   COMMENT ON COLUMN "DTL_BPM_INST_MASTER"."PROCESS_APP_ID" IS 'ProcessAppID của instance từ BPM';
   COMMENT ON COLUMN "DTL_BPM_INST_MASTER"."PROCESS_NAME" IS 'Tên luồng: SAALOP PROCESS, LOS AA...';
   COMMENT ON COLUMN "DTL_BPM_INST_MASTER"."PULL_TIME" IS 'Thời gian kéo instance về database';
   COMMENT ON COLUMN "DTL_BPM_INST_MASTER"."STARTER_ID" IS 'Info của instance từ BPM';