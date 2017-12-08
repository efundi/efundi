-- remove foreign keys
alter table CM_LECTURER drop foreign key FK_YEAR_CAMPUS_F_ID;
alter table CM_MODULES drop foreign key FK_LECTURER_F_ID;

-- drop tables
drop table if exists CM_YEAR_CAMPUS;
drop table if exists CM_LECTURER;
drop table if exists CM_MODULES;

-- create tables
CREATE TABLE CM_YEAR_CAMPUS (                           
                  YEAR_CAMPUS_ID bigint(20) NOT NULL auto_increment,    
                  YEAR int(4) NOT NULL,                                 
                  CAMPUS_CODE varchar(4) NOT NULL,               
                  PRIMARY KEY  (YEAR_CAMPUS_ID));

CREATE TABLE CM_LECTURER (                              
               LECTURER_ID bigint(20) NOT NULL auto_increment,       
               YEAR_CAMPUS_F_ID bigint(20) NOT NULL,                 
               USERNAME varchar(64) NOT NULL,                        
               PRIMARY KEY  (LECTURER_ID));

CREATE TABLE CM_MODULES (                               
              MODULE_ID bigint(20) NOT NULL auto_increment,         
              LECTURER_F_ID bigint(20) NOT NULL,                    
              COURSE_CODE varchar(16) NOT NULL,                    
              COURSE_LEVEL varchar(8) NOT NULL,                   
              COURSE_MODULE varchar(8) NOT NULL,
              STATUS varchar(32) NOT NULL,
              PRIMARY KEY  (MODULE_ID));

-- add foreign keys
alter table CM_LECTURER add index FK_YEAR_CAMPUS_F_ID (YEAR_CAMPUS_F_ID), add constraint FK_YEAR_CAMPUS_F_ID foreign key (YEAR_CAMPUS_F_ID) references CM_YEAR_CAMPUS (YEAR_CAMPUS_ID);
alter table CM_MODULES add index FK_LECTURER_F_ID (LECTURER_F_ID), add constraint FK_LECTURER_F_ID foreign key (LECTURER_F_ID) references CM_LECTURER (LECTURER_ID);

