-- remove foreign keys
alter table CM_LECTURER drop FK_YEAR_CAMPUS_F_ID
GO

alter table CM_MODULES drop FK_LECTURER_F_ID
GO

-- drop tables
if exists (select TABLE_NAME from INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='CM_YEAR_CAMPUS') DROP TABLE CM_YEAR_CAMPUS
GO

if exists (select TABLE_NAME from INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='CM_LECTURER') DROP TABLE CM_LECTURER
GO

if exists (select TABLE_NAME from INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='CM_MODULES') DROP TABLE CM_MODULES
GO

-- create tables

create table CM_YEAR_CAMPUS (YEAR_CAMPUS_ID bigint identity(1,1) primary key, 
			  YEAR int not null, 
              CAMPUS_CODE int not null)
GO

create table CM_LECTURER (LECTURER_ID bigint identity(1,1) primary key, 
		       YEAR_CAMPUS_F_ID bigint not null, 
		       USERNAME varchar(64) not null)
GO


create table CM_MODULES (MODULE_ID bigint identity(1,1) primary key, 
		     LECTURER_F_ID bigint not null, 
             COURSE_CODE varchar(16) not null, 
		     COURSE_LEVEL varchar(8) not null, 
		     COURSE_MODULE varchar(8) not null,
		     STATUS varchar(32) not null)
GO

-- create indexes
CREATE INDEX YEAR_CAMPUS_F_ID_INDEX
   ON CM_LECTURER (LECTURER_F_ID)
GO


CREATE INDEX LECTURER_F_ID_INDEX
   ON CM_MODULES (LECTURER_F_ID)
GO

-- add foreign keys
alter table CM_LECTURER add constraint FK_YEAR_CAMPUS_F_ID foreign key (YEAR_CAMPUS_F_ID) references CM_YEAR_CAMPUS (YEAR_CAMPUS_ID);
alter table CM_MODULES add constraint FK_LECTURER_F_ID foreign key (LECTURER_F_ID) references CM_LECTURER (LECTURER_ID);

