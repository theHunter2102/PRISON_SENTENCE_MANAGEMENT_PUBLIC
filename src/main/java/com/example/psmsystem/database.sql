SELECT * FROM prisoner_sentence.sentences;drop database prisoner_sentence;
create database prisoner_sentence;
use prisoner_sentence;
-- 2
create table users(
                      user_id int auto_increment primary key, -- id
                      full_name nvarchar(50) not null, -- full name
                      username nvarchar(15) BINARY not null, -- username
                      password nvarchar(500) not null -- hash password
);
create table roles(
                      role_id int auto_increment primary key,
                      name nvarchar(500) not null
);
insert into roles(name) values('PRISONER_MANAGEMENT'),('HEALTH_EXAMINER'),('VISIT_CONTROL'),('ULTIMATE_AUTHORITY');
create table user_role(
                          user_id int,
                          role_id int,
                          primary key (user_id,role_id) ,
                          foreign key (user_id) references users(user_id),
                          foreign key (role_id) references roles(role_id)
);

insert into users(full_name,username,password) values ('admin nè','admin','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5'),
                                                      ('health nè','health','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5'),
                                                      ('visit nè','visit','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5'),
                                                      ('prison nè','prison','5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5');
insert into user_role(user_id,role_id) values(1,4),(2,3),(2,2),(3,3),(4,1);
-- 2
create table prisoners(  -- tù nhân
                          prisoner_id int primary key auto_increment, -- id
                          prisoner_name nvarchar(50) not null, -- tên tù nhân
                          date_birth date not null, -- ngày sinh
                          gender int not null, -- 1: male,2:female,3:other
                          identity_card nvarchar(12) not null,
                          contacter_name nvarchar(50), -- người liên hệ
                          contacter_phone nvarchar(15), -- số điện thoại người liên hệ
                          image text not null, -- ảnh tù nhân
                          status boolean default false -- false: tại giam, true: ra tù
);
-- 3
create table sentences( -- tù án
                          sentence_id int primary key auto_increment, -- id
                          prisoner_id int not null, -- id tù nhân
                          sentences_code int not null, -- mã tù án
                          sentence_type enum ("life imprisonment",'limited time'), -- kiểu tù án(chung thân hoặc có thời hạn)
                          crimes_code text,
                          start_date date, -- ngày bắt đầu
                          end_date date, -- ngày kết thúc dự kiến
                          release_date date, -- ngày kết thúc thực tế (chỉ khi đã ra tù)
                          status boolean default false,  -- trạng thái tù án (false: đang hiệu lực, true: đã kết thúc)
                          parole_eligibility nvarchar(500), -- đề xuất cho tù án: giảm án, tăng án, biệt giam...
                          update_date date default (current_date()), -- ngày update
                          foreign key (prisoner_id) references prisoners(prisoner_id)
);
-- 4

create table crimes( -- tội danh
                       crime_id int primary key auto_increment, -- id
                       crime_name nvarchar(255) not null -- tên tội danh
);
-- 4
create table disciplinary_measures( -- phê bình, khiển trách
                                      disciplinary_measure_id int primary key auto_increment, -- id
                                      sentence_id int not null, -- id tù án
                                      date_of_occurrence date default (curdate()), -- ngày xảy ra
                                      level int, -- 1: MILD, 2:MODERATE, 3:SEVERE, 4:EXTREMELY SEVERE
                                      note text,
                                      foreign key (sentence_id) references sentences(sentence_id)
);
-- 4
create table commendations( -- tuyên dương, khen thưởng
                              commendation_id int primary key auto_increment, -- id
                              sentence_id int not null, -- id tù án
                              date_of_occurrence date default (curdate()), -- ngày xảy ra
                              level int, -- 1: MILD,2:MODERATE,3:Good,4:very good
                              note text,
                              foreign key (sentence_id) references sentences(sentence_id)
);
-- 4
create table visit_log(
                          Visit_log_id int primary key auto_increment, -- id
                          sentence_id int not null, -- id tù án
                          prisoner_id int not null, -- id tù nhân
                          visitor_name nvarchar(50) not null, -- tên người thăm
                          identity_card nvarchar(15) not null, -- CCCD
                          relationship nvarchar(50) not null, -- quan hệ
                          visit_date date default (curdate()), -- ngày thăm
                          start_time time not null , -- giờ bắt đầu thăm
                          end_time time not null, -- giờ kết thúc
                          notes text,
                          foreign key (sentence_id) references sentences(sentence_id),
                          foreign key (prisoner_id) references prisoners(prisoner_id)
);
-- 4
create table healths( -- lịch sử khám sức khỏe
                        health_id int primary key auto_increment, -- id
                        health_code varchar(10),
                        sentence_id int not null, -- id tù án
                        prisoner_id int not null, -- id tù nhân
                        weight double not null, -- kg
                        height double not null, -- mét
                        checkup_date date default (curdate()), -- ngày kiểm tra
                        status boolean default false,-- false: khỏe, true: bệnh
                        level int default 0,-- 0: không bệnh, 1: nhẹ, 2: nặng, 3: cần can thiệp
                        foreign key (prisoner_id) references prisoners(prisoner_id),
                        foreign key (sentence_id) references sentences(sentence_id)
);

create table incareration_process(
                                     process_id int primary key auto_increment,
                                     process_code varchar(10),
                                     sentence_id int not null,
                                     prisoner_id int not null,
                                     date_of_occurrence date default (curdate()),
                                     event_type enum("Breach of discipline","Bonus"),
                                     level int, -- 1: MILD,2:MODERATE,3:Good,4:very good
                                     note text,
                                     foreign key (sentence_id) references sentences(sentence_id)
);

CREATE TABLE update_log (
                            id INT PRIMARY KEY auto_increment,
                            date_update DATETIME,
                            note text,
                            user_id int,
                            foreign key (user_id) references users(user_id)
);

-- Thêm dữ liệu vào bảng prisoners
INSERT INTO prisoners (prisoner_name, date_birth, gender, identity_card, contacter_name, contacter_phone, image, status) VALUES
                                                                                                                             ('Nguyen Van A1', '1979-01-01', 1, '023456349011', 'Nguyen Van B1', '0987654321', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man1.png', false),
                                                                                                                             ('Nguyen Van A2', '1996-02-02', 1, '034456459012', 'Nguyen Van B2', '0987654322', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man2.png', false),
                                                                                                                             ('Nguyen Van A3', '2003-03-03', 1, '823456679013', 'Nguyen Van B3', '0987654323', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man3.png', false),
                                                                                                                             ('Nguyen Van A4', '1987-04-04', 1, '028756789014', 'Nguyen Van B4', '0987654324', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man4.png', false),
                                                                                                                             ('Nguyen Van A5', '1993-05-05', 1, '623756789015', 'Nguyen Van B5', '0987654325', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man5.png', false),
                                                                                                                             ('Nguyen Van A6', '1989-06-06', 1, '323456789016', 'Nguyen Van B6', '0987344326', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man6.png', false),
                                                                                                                             ('Nguyen Van A7', '1999-07-07', 1, '423456789017', 'Nguyen Van B7', '0987564327', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man7.png', false),
                                                                                                                             ('Nguyen Van A8', '1996-08-08', 1, '723456789018', 'Nguyen Van B8', '0987234328', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man8.png', false),
                                                                                                                             ('Nguyen Van A9', '1987-09-09', 1, '543456789019', 'Nguyen Van B9', '0987654329', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man9.png', false),
                                                                                                                             ('Nguyen Van A10', '1990-10-10', 1, '823456789010', 'Nguyen Van B10', '0912654330', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man10.png', false),
                                                                                                                             ('Nguyen Van A11', '1988-11-11', 1, '623456789011', 'Nguyen Van B11', '0987654331', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man11.png', false),
                                                                                                                             ('Nguyen Van A12', '1983-12-12', 1, '983456789012', 'Nguyen Van B12', '0987654332', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man12.png', false),
                                                                                                                             ('Nguyen Van A13', '1986-01-13', 1, '623456789013', 'Nguyen Van B13', '0987654333', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man13.png', false),
                                                                                                                             ('Nguyen Van A14', '1994-02-14', 1, '323456789014', 'Nguyen Van B14', '0987654334', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man14.png', false),
                                                                                                                             ('Nguyen Van A15', '1998-03-15', 1, '223456789015', 'Nguyen Van B15', '0987654335', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man15.png', false),
                                                                                                                             ('Nguyen Van A16', '1982-04-16', 1, '123456789016', 'Nguyen Van B16', '0987654336', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man16.png', false),
                                                                                                                             ('Nguyen Van A17', '1995-05-17', 1, '123456789017', 'Nguyen Van B17', '0987654337', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man17.png', false),
                                                                                                                             ('Nguyen Van A18', '1986-06-18', 1, '123456789018', 'Nguyen Van B18', '0987654338', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man18.png', false),
                                                                                                                             ('Nguyen Van A19', '1987-07-19', 1, '123456789019', 'Nguyen Van B19', '0987654339', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man19.png', false),
                                                                                                                             ('Nguyen Van A20', '1991-08-20', 1, '123456789020', 'Nguyen Van B20', '0987654340', 'src/main/resources/com/example/psmsystem/imagesPrisoner/man20.png', false),
                                                                                                                             ('Nguyen Van A21', '1973-01-01', 1, '223456789021', 'Nguyen Van B21', '0987654341', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan1.png', false),
                                                                                                                             ('Nguyen Van A22', '1972-02-02', 1, '223456789022', 'Nguyen Van B22', '0987654342', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan2.png', false),
                                                                                                                             ('Nguyen Van A23', '1971-03-03', 1, '223456789023', 'Nguyen Van B23', '0987654343', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan3.png', false),
                                                                                                                             ('Nguyen Van A24', '1970-04-04', 1, '223456789024', 'Nguyen Van B24', '0987654344', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan4.png', false),
                                                                                                                             ('Nguyen Van A25', '1969-05-05', 1, '223456789025', 'Nguyen Van B25', '0987654345', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan5.png', false),
                                                                                                                             ('Nguyen Van A26', '1968-06-06', 1, '223456789026', 'Nguyen Van B26', '0987654346', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan6.png', false),
                                                                                                                             ('Nguyen Van A27', '1967-07-07', 1, '223456789027', 'Nguyen Van B27', '0987654347', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan7.png', false),
                                                                                                                             ('Nguyen Van A28', '1966-08-08', 1, '223456789028', 'Nguyen Van B28', '0987654348', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan8.png', false),
                                                                                                                             ('Nguyen Van A29', '1965-09-09', 1, '223456789029', 'Nguyen Van B29', '0987654349', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan9.png', false),
                                                                                                                             ('Nguyen Van A30', '1964-10-10', 1, '223456789030', 'Nguyen Van B30', '0987654350', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan10.png', false),
                                                                                                                             ('Nguyen Van A31', '1963-11-11', 1, '223456789031', 'Nguyen Van B31', '0987654351', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan11.png', false),
                                                                                                                             ('Nguyen Van A32', '1962-12-12', 1, '223456789032', 'Nguyen Van B32', '0987654352', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan12.png', false),
                                                                                                                             ('Nguyen Van A33', '1961-01-13', 1, '223456789033', 'Nguyen Van B33', '0987654353', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan13.png', false),
                                                                                                                             ('Nguyen Van A34', '1960-02-14', 1, '223456789034', 'Nguyen Van B34', '0987654354', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan14.png', false),
                                                                                                                             ('Nguyen Van A35', '1959-03-15', 1, '223456789035', 'Nguyen Van B35', '0987654355', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan15.png', false),
                                                                                                                             ('Nguyen Van A36', '1958-04-16', 1, '223456789036', 'Nguyen Van B36', '0987654356', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan16.png', false),
                                                                                                                             ('Nguyen Van A37', '1957-05-17', 1, '223456789037', 'Nguyen Van B37', '0987654357', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan17.png', false),
                                                                                                                             ('Nguyen Van A38', '1956-06-18', 1, '223456789038', 'Nguyen Van B38', '0987654358', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan18.png', false),
                                                                                                                             ('Nguyen Van A39', '1955-07-19', 1, '223456789039', 'Nguyen Van B39', '0987654359', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan19.png', false),
                                                                                                                             ('Nguyen Van A40', '1954-08-20', 1, '223456789040', 'Nguyen Van B40', '0987654360', 'src/main/resources/com/example/psmsystem/imagesPrisoner/oldMan20.png', false),
                                                                                                                             ('Nguyen Thi B1', '2008-01-01', 0, '423456789041', 'Nguyen Van B41', '0987654361', 'src/main/resources/com/example/psmsystem/imagesPrisoner/woman1.png', false),
                                                                                                                             ('Nguyen Thi B2', '2007-02-02', 0, '423456789042', 'Nguyen Van B42', '0987654362', 'src/main/resources/com/example/psmsystem/imagesPrisoner/woman2.png', false),
                                                                                                                             ('Nguyen Thi B3', '2006-03-03', 0, '423456789043', 'Nguyen Van B43', '0987654363', 'src/main/resources/com/example/psmsystem/imagesPrisoner/woman3.png', false),
                                                                                                                             ('Nguyen Thi B4', '2005-04-04', 0, '423456789044', 'Nguyen Van B44', '0987654364', 'src/main/resources/com/example/psmsystem/imagesPrisoner/woman4.png', false),
                                                                                                                             ('Nguyen Thi B5', '2004-05-05', 0, '423456789045', 'Nguyen Van B45', '0987654365', 'src/main/resources/com/example/psmsystem/imagesPrisoner/woman5.png', false),
                                                                                                                             ('Nguyen Thi B6', '2003-06-06', 0, '423456789046', 'Nguyen Van B46', '0987654366', 'src/main/resources/com/example/psmsystem/imagesPrisoner/woman6.png', false),
                                                                                                                             ('Nguyen Thi B7', '2002-07-07', 0, '423456789047', 'Nguyen Van B47', '0987654367', 'src/main/resources/com/example/psmsystem/imagesPrisoner/woman7.png', false),
                                                                                                                             ('Nguyen Thi B8', '2001-08-08', 0, '423456789048', 'Nguyen Van B48', '0987654368', 'src/main/resources/com/example/psmsystem/imagesPrisoner/woman8.png', false),
                                                                                                                             ('Nguyen Thi B9', '2000-09-09', 0, '423456789049', 'Nguyen Van B49', '0987654369', 'src/main/resources/com/example/psmsystem/imagesPrisoner/woman9.png', false),
                                                                                                                             ('Nguyen Thi B10', '1999-10-10', 0, '423456789050', 'Nguyen Van B50', '0912654370', 'src/main/resources/com/example/psmsystem/imagesPrisoner/woman10.png', false),
                                                                                                                             ('Nguyen Thi B11', '1998-11-11', 0, '423456789051', 'Nguyen Van B51', '0987654371', 'src/main/resources/com/example/psmsystem/imagesPrisoner/woman11.png', false),
                                                                                                                             ('Nguyen Thi B12', '1997-12-12', 0, '423456789052', 'Nguyen Van B52', '0987654372', 'src/main/resources/com/example/psmsystem/imagesPrisoner/woman12.png', false),
                                                                                                                             ('Nguyen Thi B13', '1996-01-13', 0, '423456789053', 'Nguyen Van B53', '0987654373', 'src/main/resources/com/example/psmsystem/imagesPrisoner/woman13.png', false),
                                                                                                                             ('Nguyen Thi B14', '1995-02-14', 0, '423456789054', 'Nguyen Van B54', '0987654374', 'src/main/resources/com/example/psmsystem/imagesPrisoner/woman14.png', false),
                                                                                                                             ('Nguyen Thi B15', '1994-03-15', 0, '423456789055', 'Nguyen Van B55', '0987654375', 'src/main/resources/com/example/psmsystem/imagesPrisoner/woman15.png', false),
                                                                                                                             ('Nguyen Thi B16', '1993-04-16', 0, '423456789056', 'Nguyen Van B56', '0987654376', 'src/main/resources/com/example/psmsystem/imagesPrisoner/woman16.png', false);


INSERT INTO sentences (prisoner_id, sentences_code, sentence_type, crimes_code, start_date, end_date, status, parole_eligibility) VALUES
                                                                                                                                      (1, 101, 'life imprisonment', 'Theft', '2020-01-01', '9999-12-31', false, 'Eligible for parole after 20 years'),
                                                                                                                                      (2, 102, 'limited time', 'Fraud', '2012-05-10', '2031-05-10', false, 'Good behavior may reduce sentence'),
                                                                                                                                      (3, 103, 'limited time', 'Assault', '2024-08-15', '2026-08-15', false, 'May apply for parole after 5 years'),
                                                                                                                                      (4, 104, 'life imprisonment', 'Robbery', '2018-12-01', '9999-12-31', false, 'Parole possible after 25 years'),
                                                                                                                                      (5, 105, 'limited time', 'Drug Trafficking', '2022-03-20', '2027-03-20', false, 'Parole eligibility in 3 years'),
                                                                                                                                      (6, 106, 'life imprisonment', 'Murder', '2023-07-10', '9999-12-31', false, 'Parole possible after 15 years'),
                                                                                                                                      (7, 107, 'limited time', 'Burglary', '2020-02-25', '2030-02-25', false, 'Good behavior may reduce sentence'),
                                                                                                                                      (8, 108, 'life imprisonment', 'Kidnapping', '2017-06-30', '9999-12-31', false, 'Eligible for parole after 30 years'),
                                                                                                                                      (9, 109, 'limited time', 'Arson', '1997-09-10', '2026-09-10', false, 'May apply for parole after 4 years'),
                                                                                                                                      (10, 110, 'life imprisonment', 'Embezzlement', '2015-11-15', '9999-12-31', false, 'Parole possible after 20 years'),
                                                                                                                                      (11, 111, 'limited time', 'Forgery', '2020-04-12', '2030-04-12', false, 'Parole eligibility in 5 years'),
                                                                                                                                      (12, 112, 'life imprisonment', 'Human Trafficking', '2019-11-03', '9999-12-31', false, 'Eligible for parole after 25 years'),
                                                                                                                                      (13, 113, 'limited time', 'Money Laundering', '2018-01-09', '2034-01-09', false, 'Parole eligibility in 4 years'),
                                                                                                                                      (14, 114, 'life imprisonment', 'Extortion', '2022-02-20', '9999-12-31', false, 'Eligible for parole after 20 years'),
                                                                                                                                      (15, 115, 'limited time', 'Bribery', '2022-09-14', '2031-09-14', false, 'Good behavior may reduce sentence'),
                                                                                                                                      (16, 116, 'limited time', 'Cybercrime', '2019-03-25', '2029-03-25', false, 'Parole eligibility in 6 years'),
                                                                                                                                      (17, 117, 'life imprisonment', 'Manslaughter', '2017-07-17', '9999-12-31', false, 'Eligible for parole after 20 years'),
                                                                                                                                      (18, 118, 'limited time', 'Domestic Violence', '2016-12-11', '2026-12-11', false, 'Parole eligibility in 3 years'),
                                                                                                                                      (19, 119, 'life imprisonment', 'Terrorism', '2015-05-05', '9999-12-31', false, 'Eligible for parole after 30 years'),
                                                                                                                                      (20, 120, 'limited time', 'Vandalism', '2014-10-10', '2024-10-10', false, 'Good behavior may reduce sentence'),

                                                                                                                                      (21, 121, 'life imprisonment', 'Theft', '2020-01-01', '9999-12-31', false, 'Eligible for parole after 20 years'),
                                                                                                                                      (22, 122, 'limited time', 'Fraud', '2012-05-10', '2031-05-10', false, 'Good behavior may reduce sentence'),
                                                                                                                                      (23, 123, 'limited time', 'Assault', '2024-08-15', '2026-08-15', false, 'May apply for parole after 5 years'),
                                                                                                                                      (24, 124, 'life imprisonment', 'Robbery', '2018-12-01', '9999-12-31', false, 'Parole possible after 25 years'),
                                                                                                                                      (25, 125, 'limited time', 'Drug Trafficking', '2022-03-20', '2027-03-20', false, 'Parole eligibility in 3 years'),
                                                                                                                                      (26, 126, 'life imprisonment', 'Murder', '2023-07-10', '9999-12-31', false, 'Parole possible after 15 years'),
                                                                                                                                      (27, 127, 'limited time', 'Burglary', '2020-02-25', '2038-02-25', false, 'Good behavior may reduce sentence'),
                                                                                                                                      (28, 128, 'life imprisonment', 'Kidnapping', '2017-06-30', '9999-12-31', false, 'Eligible for parole after 30 years'),
                                                                                                                                      (29, 129, 'limited time', 'Arson', '1997-09-10', '2026-09-10', false, 'May apply for parole after 4 years'),
                                                                                                                                      (30, 130, 'life imprisonment', 'Embezzlement', '2015-11-15', '9999-12-31', false, 'Parole possible after 20 years'),
                                                                                                                                      (31, 131, 'limited time', 'Forgery', '2020-04-12', '2046-04-12', false, 'Parole eligibility in 5 years'),
                                                                                                                                      (32, 132, 'life imprisonment', 'Human Trafficking', '2019-11-03', '9999-12-31', false, 'Eligible for parole after 25 years'),
                                                                                                                                      (33, 133, 'limited time', 'Money Laundering', '2018-01-09', '2034-01-09', false, 'Parole eligibility in 4 years'),
                                                                                                                                      (34, 134, 'life imprisonment', 'Extortion', '2022-02-20', '9999-12-31', false, 'Eligible for parole after 20 years'),
                                                                                                                                      (35, 135, 'limited time', 'Bribery', '2022-09-14', '2031-09-14', false, 'Good behavior may reduce sentence'),
                                                                                                                                      (36, 136, 'limited time', 'Cybercrime', '2019-03-25', '2059-03-25', false, 'Parole eligibility in 6 years'),
                                                                                                                                      (37, 137, 'life imprisonment', 'Manslaughter', '2017-07-17', '9999-12-31', false, 'Eligible for parole after 20 years'),
                                                                                                                                      (38, 138, 'limited time', 'Domestic Violence', '2016-12-11', '2026-12-11', false, 'Parole eligibility in 3 years'),
                                                                                                                                      (39, 139, 'life imprisonment', 'Terrorism', '2015-05-05', '9999-12-31', false, 'Eligible for parole after 30 years'),
                                                                                                                                      (40, 140, 'limited time', 'Vandalism', '2014-10-10', '2021-10-10', false, 'Good behavior may reduce sentence'),
                                                                                                                                      (41, 141, 'life imprisonment', 'Theft', '2020-01-01', '9999-12-31', false, 'Eligible for parole after 20 years'),
                                                                                                                                      (42, 142, 'limited time', 'Fraud', '2012-05-10', '2031-05-10', false, 'Good behavior may reduce sentence'),
                                                                                                                                      (43, 143, 'limited time', 'Assault', '2024-08-15', '2026-08-15', false, 'May apply for parole after 5 years'),
                                                                                                                                      (44, 144, 'life imprisonment', 'Robbery', '2018-12-01', '9999-12-31', false, 'Parole possible after 25 years'),
                                                                                                                                      (45, 145, 'limited time', 'Drug Trafficking', '2022-03-20', '2027-03-20', false, 'Parole eligibility in 3 years'),
                                                                                                                                      (46, 146, 'life imprisonment', 'Murder', '2023-07-10', '9999-12-31', false, 'Parole possible after 15 years'),
                                                                                                                                      (47, 147, 'limited time', 'Burglary', '2020-02-25', '2038-02-25', false, 'Good behavior may reduce sentence'),
                                                                                                                                      (48, 148, 'life imprisonment', 'Kidnapping', '2017-06-30', '9999-12-31', false, 'Eligible for parole after 30 years'),
                                                                                                                                      (49, 149, 'limited time', 'Arson', '1997-09-10', '2026-09-10', false, 'May apply for parole after 4 years'),
                                                                                                                                      (50, 150, 'life imprisonment', 'Embezzlement', '2015-11-15', '9999-12-31', false, 'Parole possible after 20 years'),
                                                                                                                                      (51, 151, 'limited time', 'Forgery', '2020-04-12', '2046-04-12', false, 'Parole eligibility in 5 years'),
                                                                                                                                      (52, 152, 'life imprisonment', 'Human Trafficking', '2019-11-03', '9999-12-31', false, 'Eligible for parole after 25 years'),
                                                                                                                                      (53, 153, 'limited time', 'Money Laundering', '2018-01-09', '2034-01-09', false, 'Parole eligibility in 4 years'),
                                                                                                                                      (54, 154, 'life imprisonment', 'Extortion', '2022-02-20', '9999-12-31', false, 'Eligible for parole after 20 years'),
                                                                                                                                      (55, 155, 'limited time', 'Bribery', '2022-09-14', '2031-09-14', false, 'Good behavior may reduce sentence'),
                                                                                                                                      (56, 156, 'limited time', 'Cybercrime', '2019-03-25', '2059-03-25', false, 'Parole eligibility in 6 years');


INSERT INTO disciplinary_measures (sentence_id, date_of_occurrence, level, note) VALUES
                                                                                     (1, '2023-01-20', 3, 'Violent behavior'),
                                                                                     (2, '2020-12-05', 4, 'Severe violation of rules'),
                                                                                     (3, '2019-07-25', 2, 'Disobeying orders'),
                                                                                     (4, '2018-04-30', 3, 'Fighting with other inmates'),
                                                                                     (5, '2022-10-15', 1, 'Late for roll call'),
                                                                                     (6, '2021-02-28', 4, 'Attempted escape'),
                                                                                     (7, '2019-11-10', 3, 'Threatening staff'),
                                                                                     (8, '2018-05-22', 1, 'Unauthorized communication'),
                                                                                     (9, '2021-03-14', 4, 'Assaulting an officer'),
                                                                                     (10, '2020-10-07', 3, 'Inciting a riot'),
                                                                                     (11, '2019-12-03', 1, 'Failure to comply'),
                                                                                     (12, '2018-08-18', 4, 'Damage to property'),
                                                                                     (13, '2023-04-09', 2, 'Contraband smuggling'),
                                                                                     (14, '2022-11-30', 3, 'Forgery of documents'),
                                                                                     (15, '2021-09-21', 1, 'Misuse of privileges'),
                                                                                     (16, '2020-07-14', 4, 'Creating disturbances'),
                                                                                     (17, '2023-01-20', 3, 'Violent behavior'),
                                                                                     (18, '2021-06-10', 1, 'Minor misconduct'),
                                                                                     (19, '2019-07-25', 2, 'Disobeying orders'),
                                                                                     (20, '2018-04-30', 3, 'Fighting with other inmates'),
                                                                                     (21, '2022-10-15', 1, 'Late for roll call'),
                                                                                     (22, '2021-02-28', 4, 'Attempted escape'),
                                                                                     (23, '2019-11-10', 3, 'Threatening staff'),
                                                                                     (24, '2018-05-22', 1, 'Unauthorized communication'),
                                                                                     (25, '2021-03-14', 4, 'Assaulting an officer'),
                                                                                     (26, '2020-10-07', 3, 'Inciting a riot'),
                                                                                     (27, '2019-12-03', 1, 'Failure to comply'),
                                                                                     (28, '2018-08-18', 4, 'Damage to property'),
                                                                                     (29, '2023-04-09', 2, 'Contraband smuggling'),
                                                                                     (30, '2022-11-30', 3, 'Forgery of documents'),
                                                                                     (31, '2020-07-14', 4, 'Creating disturbances'),
                                                                                     (32, '2022-03-15', 2, 'Insubordination'),
                                                                                     (33, '2023-01-20', 3, 'Violent behavior'),
                                                                                     (34, '2021-06-10', 1, 'Minor misconduct'),
                                                                                     (35, '2019-07-25', 2, 'Disobeying orders'),
                                                                                     (36, '2018-04-30', 3, 'Fighting with other inmates'),
                                                                                     (37, '2022-10-15', 1, 'Late for roll call'),
                                                                                     (38, '2021-02-28', 4, 'Attempted escape'),
                                                                                     (39, '2019-11-10', 3, 'Threatening staff'),
                                                                                     (40, '2018-05-22', 1, 'Unauthorized communication'),
                                                                                     (41, '2023-01-20', 3, 'Violent behavior'),
                                                                                     (42, '2020-12-05', 4, 'Severe violation of rules'),
                                                                                     (43, '2019-07-25', 2, 'Disobeying orders'),
                                                                                     (44, '2018-04-30', 3, 'Fighting with other inmates'),
                                                                                     (45, '2022-10-15', 1, 'Late for roll call'),
                                                                                     (46, '2021-02-28', 4, 'Attempted escape'),
                                                                                     (47, '2019-11-10', 3, 'Threatening staff'),
                                                                                     (48, '2018-05-22', 1, 'Unauthorized communication'),
                                                                                     (49, '2021-03-14', 4, 'Assaulting an officer'),
                                                                                     (50, '2020-10-07', 3, 'Inciting a riot'),
                                                                                     (51, '2019-12-03', 1, 'Failure to comply'),
                                                                                     (52, '2018-08-18', 4, 'Damage to property'),
                                                                                     (53, '2023-04-09', 2, 'Contraband smuggling'),
                                                                                     (54, '2022-11-30', 3, 'Forgery of documents'),
                                                                                     (55, '2021-09-21', 1, 'Misuse of privileges'),
                                                                                     (56, '2020-07-14', 4, 'Creating disturbances');
INSERT INTO commendations (sentence_id, date_of_occurrence, level, note) VALUES
                                                                             (1, '2022-06-30', 4, 'Helped in prison work'),
                                                                             (2, '2023-02-25', 3, 'Good behavior'),
                                                                             (3, '2021-09-15', 2, 'Assisted in a charity event'),
                                                                             (4, '2020-05-10', 1, 'Maintained cleanliness'),
                                                                             (5, '2019-03-25', 4, 'Prevented an escape attempt'),
                                                                             (6, '2018-08-30', 3, 'Participated in educational program'),
                                                                             (7, '2022-11-15', 2, 'Showed leadership among inmates'),
                                                                             (8, '2021-01-28', 1, 'Completed vocational training'),
                                                                             (9, '2020-06-20', 4, 'Provided medical assistance'),
                                                                             (10, '2019-12-10', 3, 'Good conduct during lockdown'),
                                                                             (11, '2018-10-05', 2, 'Excellent work in kitchen duty'),
                                                                             (12, '2021-07-14', 1, 'Participated in sports activities'),
                                                                             (13, '2020-02-11', 4, 'Helped diffuse a conflict'),
                                                                             (14, '2019-11-03', 3, 'Outstanding performance in workshop'),
                                                                             (15, '2018-05-22', 2, 'Acted as mediator among inmates'),
                                                                             (16, '2022-09-19', 1, 'Volunteer in prison library'),
                                                                             (17, '2021-04-30', 4, 'Developed a training program'),
                                                                             (18, '2020-08-15', 3, 'Helped organize an event'),
                                                                             (19, '2019-06-25', 2, 'Excellent behavior'),
                                                                             (20, '2018-12-21', 1, 'Assisted with clerical tasks'),
                                                                             (21, '2022-06-30', 4, 'Helped in prison work'),
                                                                             (22, '2023-02-25', 3, 'Good behavior'),
                                                                             (23, '2021-09-15', 2, 'Assisted in a charity event'),
                                                                             (24, '2020-05-10', 1, 'Maintained cleanliness'),
                                                                             (25, '2019-03-25', 4, 'Prevented an escape attempt'),
                                                                             (26, '2018-08-30', 3, 'Participated in educational program'),
                                                                             (27, '2022-11-15', 2, 'Showed leadership among inmates'),
                                                                             (28, '2021-01-28', 1, 'Completed vocational training'),
                                                                             (29, '2020-06-20', 4, 'Provided medical assistance'),
                                                                             (30, '2019-12-10', 3, 'Good conduct during lockdown'),
                                                                             (31, '2018-10-05', 2, 'Excellent work in kitchen duty'),
                                                                             (32, '2021-07-14', 1, 'Participated in sports activities'),
                                                                             (33, '2020-02-11', 4, 'Helped diffuse a conflict'),
                                                                             (34, '2019-11-03', 3, 'Outstanding performance in workshop'),
                                                                             (35, '2018-05-22', 2, 'Acted as mediator among inmates'),
                                                                             (36, '2022-09-19', 1, 'Volunteer in prison library'),
                                                                             (37, '2021-04-30', 4, 'Developed a training program'),
                                                                             (38, '2020-08-15', 3, 'Helped organize an event'),
                                                                             (39, '2019-06-25', 2, 'Excellent behavior'),
                                                                             (40, '2018-12-21', 1, 'Assisted with clerical tasks'),
                                                                             (41, '2022-06-30', 4, 'Helped in prison work'),
                                                                             (42, '2023-02-25', 3, 'Good behavior'),
                                                                             (43, '2021-09-15', 2, 'Assisted in a charity event'),
                                                                             (44, '2020-05-10', 1, 'Maintained cleanliness'),
                                                                             (45, '2019-03-25', 4, 'Prevented an escape attempt'),
                                                                             (46, '2018-08-30', 3, 'Participated in educational program'),
                                                                             (47, '2022-11-15', 2, 'Showed leadership among inmates'),
                                                                             (48, '2021-01-28', 1, 'Completed vocational training'),
                                                                             (49, '2020-06-20', 4, 'Provided medical assistance'),
                                                                             (50, '2019-12-10', 3, 'Good conduct during lockdown'),
                                                                             (51, '2018-10-05', 2, 'Excellent work in kitchen duty'),
                                                                             (52, '2021-07-14', 1, 'Participated in sports activities'),
                                                                             (53, '2020-02-11', 4, 'Helped diffuse a conflict'),
                                                                             (54, '2019-11-03', 3, 'Outstanding performance in workshop'),
                                                                             (55, '2018-05-22', 2, 'Acted as mediator among inmates'),
                                                                             (56, '2022-09-19', 1, 'Volunteer in prison library');

INSERT INTO visit_log (sentence_id, prisoner_id, visitor_name, identity_card, relationship, visit_date, start_time, end_time, notes) VALUES
                                                                                                                                         (1, 1, 'Nguyen Van B', '111122223333', 'Brother', '2023-04-10', '09:00:00', '10:00:00', 'Brought food and clothes'),
                                                                                                                                         (2, 2, 'Tran Van C', '444455556666', 'Father', '2023-05-15', '14:00:00', '15:00:00', 'Discussed legal matters'),
                                                                                                                                         (3, 3, 'Le Thi D', '777788889999', 'Wife', '2023-03-20', '10:00:00', '11:00:00', 'Discussed family issues'),
                                                                                                                                         (4, 4, 'Pham Van E', '000011112222', 'Husband', '2023-02-25', '15:00:00', '16:00:00', 'Brought documents'),
                                                                                                                                         (5, 5, 'Hoang Thi F', '333344445555', 'Sister', '2023-01-15', '11:00:00', '12:00:00', 'Provided moral support'),
                                                                                                                                         (6, 6, 'Vu Van G', '666677778888', 'Brother', '2023-06-10', '09:30:00', '10:30:00', 'Discussed health issues'),
                                                                                                                                         (7, 7, 'Ngo Thi H', '999900001111', 'Wife', '2023-07-20', '13:00:00', '14:00:00', 'Brought clothes'),
                                                                                                                                         (8, 8, 'Dang Van I', '222233334444', 'Father', '2023-08-30', '14:30:00', '15:30:00', 'Discussed parole options'),
                                                                                                                                         (9, 9, 'Bui Thi J', '555566667777', 'Mother', '2023-09-25', '10:00:00', '11:00:00', 'Discussed family matters'),
                                                                                                                                         (10, 10, 'Tran Thi K', '888899990000', 'Wife', '2023-10-15', '16:00:00', '17:00:00', 'Brought legal documents'),
                                                                                                                                         (11, 11, 'Nguyen Van L', '111122220001', 'Brother', '2023-11-20', '09:00:00', '10:00:00', 'Brought food and clothes'),
                                                                                                                                         (12, 12, 'Tran Van M', '444455556002', 'Father', '2023-12-15', '14:00:00', '15:00:00', 'Discussed legal matters'),
                                                                                                                                         (13, 13, 'Le Thi N', '777788889003', 'Wife', '2023-12-20', '10:00:00', '11:00:00', 'Discussed family issues'),
                                                                                                                                         (14, 14, 'Pham Van O', '000011112004', 'Husband', '2023-12-25', '15:00:00', '16:00:00', 'Brought documents'),
                                                                                                                                         (15, 15, 'Hoang Thi P', '333344445005', 'Sister', '2023-12-15', '11:00:00', '12:00:00', 'Provided moral support'),
                                                                                                                                         (16, 16, 'Vu Van Q', '666677778006', 'Brother', '2023-12-10', '09:30:00', '10:30:00', 'Discussed health issues'),
                                                                                                                                         (17, 17, 'Ngo Thi R', '999900001007', 'Wife', '2023-12-20', '13:00:00', '14:00:00', 'Brought clothes'),
                                                                                                                                         (18, 18, 'Dang Van S', '222233334008', 'Father', '2023-12-30', '14:30:00', '15:30:00', 'Discussed parole options'),
                                                                                                                                         (19, 19, 'Bui Thi T', '555566667009', 'Mother', '2023-12-25', '10:00:00', '11:00:00', 'Discussed family matters'),
                                                                                                                                         (20, 20, 'Tran Thi U', '888899990010', 'Wife', '2023-12-15', '16:00:00', '17:00:00', 'Brought legal documents'),
                                                                                                                                         (21, 21, 'Nguyen Van V', '111122223334', 'Brother', '2024-01-10', '09:00:00', '10:00:00', 'Brought food and clothes'),
                                                                                                                                         (22, 22, 'Tran Van W', '444455556667', 'Father', '2024-02-15', '14:00:00', '15:00:00', 'Discussed legal matters'),
                                                                                                                                         (23, 23, 'Le Thi X', '777788889991', 'Wife', '2024-03-20', '10:00:00', '11:00:00', 'Discussed family issues'),
                                                                                                                                         (24, 24, 'Pham Van Y', '000011112223', 'Husband', '2024-04-25', '15:00:00', '16:00:00', 'Brought documents'),
                                                                                                                                         (25, 25, 'Hoang Thi Z', '333344445566', 'Sister', '2024-05-15', '11:00:00', '12:00:00', 'Provided moral support'),
                                                                                                                                         (26, 26, 'Vu Van A', '666677778899', 'Brother', '2024-06-10', '09:30:00', '10:30:00', 'Discussed health issues'),
                                                                                                                                         (27, 27, 'Ngo Thi B', '999900001112', 'Wife', '2024-07-20', '13:00:00', '14:00:00', 'Brought clothes'),
                                                                                                                                         (28, 28, 'Dang Van C', '222233334445', 'Father', '2024-08-30', '14:30:00', '15:30:00', 'Discussed parole options'),
                                                                                                                                         (29, 29, 'Bui Thi D', '555566667778', 'Mother', '2024-09-25', '10:00:00', '11:00:00', 'Discussed family matters'),
                                                                                                                                         (30, 30, 'Tran Thi E', '888899990001', 'Wife', '2024-10-15', '16:00:00', '17:00:00', 'Brought legal documents'),
                                                                                                                                         (31, 31, 'Nguyen Van F', '111122220002', 'Brother', '2024-11-20', '09:00:00', '10:00:00', 'Brought food and clothes'),
                                                                                                                                         (32, 32, 'Tran Van G', '444455556003', 'Father', '2024-12-15', '14:00:00', '15:00:00', 'Discussed legal matters'),
                                                                                                                                         (33, 33, 'Le Thi H', '777788889004', 'Wife', '2024-12-20', '10:00:00', '11:00:00', 'Discussed family issues'),
                                                                                                                                         (34, 34, 'Pham Van I', '000011112005', 'Husband', '2024-12-25', '15:00:00', '16:00:00', 'Brought documents'),
                                                                                                                                         (35, 35, 'Hoang Thi J', '333344445006', 'Sister', '2024-12-15', '11:00:00', '12:00:00', 'Provided moral support'),
                                                                                                                                         (36, 36, 'Vu Van K', '666677778007', 'Brother', '2024-12-10', '09:30:00', '10:30:00', 'Discussed health issues'),
                                                                                                                                         (37, 37, 'Ngo Thi L', '999900001008', 'Wife', '2024-12-20', '13:00:00', '14:00:00', 'Brought clothes'),
                                                                                                                                         (38, 38, 'Dang Van M', '222233334009', 'Father', '2024-12-30', '14:30:00', '15:30:00', 'Discussed parole options'),
                                                                                                                                         (39, 39, 'Bui Thi N', '555566667010', 'Mother', '2024-12-25', '10:00:00', '11:00:00', 'Discussed family matters'),
                                                                                                                                         (40, 40, 'Tran Thi O', '888899990011', 'Wife', '2024-12-15', '16:00:00', '17:00:00', 'Brought legal documents'),
                                                                                                                                         (41, 1, 'Nguyen Van B', '111122223333', 'Brother', '2025-01-10', '09:00:00', '10:00:00', 'Brought food and clothes'),
                                                                                                                                         (42, 2, 'Tran Van C', '444455556666', 'Father', '2025-02-15', '14:00:00', '15:00:00', 'Discussed legal matters'),
                                                                                                                                         (43, 3, 'Le Thi D', '777788889999', 'Wife', '2025-03-20', '10:00:00', '11:00:00', 'Discussed family issues'),
                                                                                                                                         (44, 4, 'Pham Van E', '000011112222', 'Husband', '2025-04-25', '15:00:00', '16:00:00', 'Brought documents'),
                                                                                                                                         (45, 5, 'Hoang Thi F', '333344445555', 'Sister', '2025-05-15', '11:00:00', '12:00:00', 'Provided moral support'),
                                                                                                                                         (46, 6, 'Vu Van G', '666677778888', 'Brother', '2025-06-10', '09:30:00', '10:30:00', 'Discussed health issues'),
                                                                                                                                         (47, 7, 'Ngo Thi H', '999900001111', 'Wife', '2025-07-20', '13:00:00', '14:00:00', 'Brought clothes'),
                                                                                                                                         (48, 8, 'Dang Van I', '222233334444', 'Father', '2025-08-30', '14:30:00', '15:30:00', 'Discussed parole options'),
                                                                                                                                         (49, 9, 'Bui Thi J', '555566667777', 'Mother', '2025-09-25', '10:00:00', '11:00:00', 'Discussed family matters'),
                                                                                                                                         (50, 10, 'Tran Thi K', '888899990000', 'Wife', '2025-10-15', '16:00:00', '17:00:00', 'Brought legal documents'),
                                                                                                                                         (51, 11, 'Nguyen Van L', '111122220001', 'Brother', '2025-11-20', '09:00:00', '10:00:00', 'Brought food and clothes'),
                                                                                                                                         (52, 12, 'Tran Van M', '444455556002', 'Father', '2025-12-15', '14:00:00', '15:00:00', 'Discussed legal matters'),
                                                                                                                                         (53, 13, 'Le Thi N', '777788889003', 'Wife', '2025-12-20', '10:00:00', '11:00:00', 'Discussed family issues'),
                                                                                                                                         (54, 14, 'Pham Van O', '000011112004', 'Husband', '2025-12-25', '15:00:00', '16:00:00', 'Brought documents'),
                                                                                                                                         (55, 15, 'Hoang Thi P', '333344445005', 'Sister', '2025-12-15', '11:00:00', '12:00:00', 'Provided moral support'),
                                                                                                                                         (56, 16, 'Vu Van Q', '666677778006', 'Brother', '2025-12-10', '09:30:00', '10:30:00', 'Discussed health issues');

INSERT INTO healths (health_code, sentence_id, prisoner_id, weight, height, checkup_date, status, level) VALUES
                                                                                                             ('H1', 1, 1, 70, 1.75, '2023-03-10', false, 0),
                                                                                                             ('H2', 2, 2, 65, 1.60, '2023-04-20', true, 1),
                                                                                                             ('H3', 3, 3, 80, 1.80, '2023-05-15', false, 0),
                                                                                                             ('H4', 4, 4, 55, 1.55, '2023-06-10', true, 2),
                                                                                                             ('H5', 5, 5, 75, 1.70, '2023-07-20', false, 0),
                                                                                                             ('H6', 6, 6, 68, 1.65, '2023-08-30', true, 1),
                                                                                                             ('H7', 7, 7, 85, 1.85, '2023-09-25', true, 3),
                                                                                                             ('H8', 8, 8, 60, 1.60, '2023-10-15', false, 0),
                                                                                                             ('H9', 9, 9, 72, 1.78, '2023-11-10', true, 2),
                                                                                                             ('H10', 10, 10, 67, 1.67, '2023-12-05', false, 0),
                                                                                                             ('H11', 11, 11, 73, 1.76, '2024-01-20', true, 1),
                                                                                                             ('H12', 12, 12, 66, 1.62, '2024-02-15', false, 0),
                                                                                                             ('H13', 13, 13, 82, 1.82, '2024-03-10', true, 2),
                                                                                                             ('H14', 14, 14, 57, 1.58, '2024-04-25', false, 0),
                                                                                                             ('H15', 15, 15, 78, 1.72, '2024-05-15', true, 1),
                                                                                                             ('H16', 16, 16, 70, 1.66, '2024-06-10', true, 3),
                                                                                                             ('H17', 17, 17, 87, 1.88, '2024-07-20', false, 0),
                                                                                                             ('H18', 18, 18, 62, 1.63, '2024-08-30', true, 2),
                                                                                                             ('H19', 19, 19, 75, 1.79, '2024-09-25', false, 0),
                                                                                                             ('H20', 20, 20, 69, 1.68, '2024-10-15', true, 1),
                                                                                                             ('H21', 21, 21, 76, 1.73, '2024-11-10', true, 2),
                                                                                                             ('H22', 22, 22, 63, 1.64, '2024-12-05', false, 0),
                                                                                                             ('H23', 23, 23, 83, 1.83, '2025-01-20', true, 3),
                                                                                                             ('H24', 24, 24, 58, 1.59, '2025-02-15', false, 0),
                                                                                                             ('H25', 25, 25, 79, 1.73, '2025-03-10', true, 1),
                                                                                                             ('H26', 26, 26, 71, 1.67, '2025-04-25', false, 0),
                                                                                                             ('H27', 27, 27, 88, 1.89, '2025-05-15', true, 2),
                                                                                                             ('H28', 28, 28, 64, 1.65, '2025-06-10', false, 0),
                                                                                                             ('H29', 29, 29, 77, 1.75, '2025-07-20', true, 1),
                                                                                                             ('H30', 30, 30, 70, 1.68, '2025-08-30', true, 3),
                                                                                                             ('H31', 31, 31, 86, 1.90, '2025-09-25', false, 0),
                                                                                                             ('H32', 32, 32, 61, 1.62, '2025-10-15', true, 2),
                                                                                                             ('H33', 33, 33, 74, 1.80, '2025-11-10', false, 0),
                                                                                                             ('H34', 34, 34, 68, 1.69, '2025-12-05', true, 1),
                                                                                                             ('H35', 35, 35, 81, 1.81, '2026-01-20', true, 2),
                                                                                                             ('H36', 36, 36, 56, 1.57, '2026-02-15', false, 0),
                                                                                                             ('H37', 37, 37, 84, 1.84, '2026-03-10', true, 1),
                                                                                                             ('H38', 38, 38, 59, 1.60, '2026-04-25', false, 0),
                                                                                                             ('H39', 39, 39, 80, 1.76, '2026-05-15', true, 3),
                                                                                                             ('H40', 40, 40, 72, 1.70, '2026-06-10', false, 0),
                                                                                                             ('H41', 1, 1, 68, 1.74, '2026-07-20', true, 1),
                                                                                                             ('H42', 2, 2, 63, 1.58, '2026-08-30', false, 0),
                                                                                                             ('H43', 3, 3, 78, 1.85, '2026-09-25', true, 2),
                                                                                                             ('H44', 4, 4, 52, 1.50, '2026-10-15', false, 0),
                                                                                                             ('H45', 5, 5, 72, 1.75, '2026-11-10', true, 3),
                                                                                                             ('H46', 6, 6, 66, 1.60, '2026-12-05', false, 0),
                                                                                                             ('H47', 7, 7, 90, 1.90, '2027-01-20', true, 1),
                                                                                                             ('H48', 8, 8, 58, 1.55, '2027-02-15', false, 0),
                                                                                                             ('H49', 9, 9, 70, 1.80, '2027-03-10', true, 2),
                                                                                                             ('H50', 10, 10, 65, 1.65, '2027-04-25', false, 0),
                                                                                                             ('H51', 11, 11, 71, 1.70, '2027-05-15', true, 3),
                                                                                                             ('H52', 12, 12, 64, 1.62, '2027-06-10', false, 0),
                                                                                                             ('H53', 13, 13, 80, 1.88, '2027-07-20', true, 1),
                                                                                                             ('H54', 14, 14, 55, 1.57, '2027-08-30', false, 0),
                                                                                                             ('H55', 15, 15, 76, 1.73, '2027-09-25', true, 2),
                                                                                                             ('H56', 16, 16, 68, 1.65, '2027-10-15', false, 0);


INSERT INTO incareration_process (process_code, sentence_id, prisoner_id, date_of_occurrence, event_type, level, note) VALUES
                                                                                                                           ('P1', 1, 1, '2024-06-01', 'Breach of discipline', 1, 'Minor misconduct.'),
                                                                                                                           ('P2', 2, 2, '2024-06-02', 'Bonus', 4, 'Exemplary behavior.'),
                                                                                                                           ('P3', 3, 3, '2024-06-03', 'Breach of discipline', 2, 'Moderate misconduct.'),
                                                                                                                           ('P4', 4, 4, '2024-06-04', 'Bonus', 3, 'Good behavior.'),
                                                                                                                           ('P6', 6, 6, '2024-06-06', 'Bonus', 4, 'Very good behavior.'),
                                                                                                                           ('P7', 7, 7, '2024-06-07', 'Breach of discipline', 4, 'Moderate misconduct again.'),
                                                                                                                           ('P8', 5, 5, '2024-06-08', 'Bonus', 4, 'Good behavior again.'),
                                                                                                                           ('P9', 9, 9, '2024-06-09', 'Breach of discipline', 1, 'Another minor misconduct.'),
                                                                                                                           ('P10', 10, 10, '2024-06-10', 'Bonus', 4, 'Outstanding behavior.'),
                                                                                                                           ('P11', 11, 11, '2024-06-11', 'Breach of discipline', 1, 'Minor misconduct.'),
                                                                                                                           ('P12', 12, 12, '2024-06-12', 'Bonus', 4, 'Excellent behavior.'),
                                                                                                                           ('P13', 13, 13, '2024-06-13', 'Breach of discipline', 2, 'Moderate misconduct.'),
                                                                                                                           ('P14', 14, 14, '2024-06-14', 'Bonus', 3, 'Good behavior.'),
                                                                                                                           ('P15', 15, 15, '2023-06-15', 'Breach of discipline', 1, 'Minor misconduct again.'),
                                                                                                                           ('P16', 16, 16, '2024-06-16', 'Bonus', 4, 'Very good behavior.'),
                                                                                                                           ('P17', 17, 17, '2024-06-17', 'Breach of discipline', 2, 'Moderate misconduct again.'),
                                                                                                                           ('P18', 18, 18, '2024-06-18', 'Bonus', 3, 'Good behavior again.'),
                                                                                                                           ('P19', 19, 19, '2024-06-19', 'Breach of discipline', 1, 'Another minor misconduct.'),
                                                                                                                           ('P20', 20, 20, '2024-06-20', 'Bonus', 4, 'Outstanding behavior.'),
                                                                                                                           ('P21', 21, 21, '2024-06-21', 'Breach of discipline', 2, 'Moderate misconduct again.'),
                                                                                                                           ('P22', 22, 22, '2024-06-22', 'Bonus', 3, 'Good behavior.'),
                                                                                                                           ('P23', 23, 23, '2024-06-23', 'Breach of discipline', 1, 'Minor misconduct again.'),
                                                                                                                           ('P24', 24, 24, '2024-06-24', 'Bonus', 4, 'Very good behavior.'),
                                                                                                                           ('P25', 25, 25, '2024-06-25', 'Breach of discipline', 2, 'Moderate misconduct again.'),
                                                                                                                           ('P26', 26, 26, '2024-06-26', 'Bonus', 3, 'Good behavior again.'),
                                                                                                                           ('P27', 27, 27, '2024-06-27', 'Breach of discipline', 1, 'Another minor misconduct.'),
                                                                                                                           ('P28', 28, 28, '2024-06-28', 'Bonus', 4, 'Outstanding behavior.'),
                                                                                                                           ('P29', 29, 29, '2024-06-29', 'Breach of discipline', 1, 'Minor misconduct.'),
                                                                                                                           ('P30', 30, 30, '2024-06-30', 'Bonus', 4, 'Excellent behavior.'),
                                                                                                                           ('P31', 31, 31, '2024-07-01', 'Breach of discipline', 2, 'Moderate misconduct.'),
                                                                                                                           ('P32', 32, 32, '2024-07-02', 'Bonus', 3, 'Good behavior.'),
                                                                                                                           ('P33', 33, 33, '2024-07-03', 'Breach of discipline', 1, 'Minor misconduct again.'),
                                                                                                                           ('P34', 34, 34, '2024-07-04', 'Bonus', 4, 'Very good behavior.'),
                                                                                                                           ('P35', 35, 35, '2024-07-05', 'Breach of discipline', 2, 'Moderate misconduct again.'),
                                                                                                                           ('P36', 36, 36, '2024-07-06', 'Bonus', 3, 'Good behavior again.'),
                                                                                                                           ('P37', 37, 37, '2024-07-07', 'Breach of discipline', 1, 'Another minor misconduct.'),
                                                                                                                           ('P38', 38, 38, '2024-07-08', 'Bonus', 4, 'Outstanding behavior.'),
                                                                                                                           ('P39', 39, 39, '2024-07-09', 'Breach of discipline', 2, 'Moderate misconduct.'),
                                                                                                                           ('P40', 40, 40, '2024-07-10', 'Bonus', 3, 'Good behavior.'),
                                                                                                                           ('P41', 1, 1, '2024-07-11', 'Breach of discipline', 1, 'Minor misconduct.'),
                                                                                                                           ('P42', 2, 2, '2024-07-12', 'Bonus', 4, 'Exemplary behavior.'),
                                                                                                                           ('P43', 3, 3, '2024-07-13', 'Breach of discipline', 2, 'Moderate misconduct.'),
                                                                                                                           ('P44', 4, 4, '2024-07-14', 'Bonus', 3, 'Good behavior.'),
                                                                                                                           ('P45', 5, 5, '2024-07-15', 'Bonus', 4, 'Good behavior again.'),
                                                                                                                           ('P46', 6, 6, '2024-07-16', 'Breach of discipline', 4, 'Moderate misconduct again.'),
                                                                                                                           ('P47', 7, 7, '2024-07-17', 'Bonus', 4, 'Good behavior.'),
                                                                                                                           ('P48', 8, 8, '2024-07-18', 'Breach of discipline', 1, 'Minor misconduct.'),
                                                                                                                           ('P49', 9, 9, '2024-07-19', 'Bonus', 4, 'Outstanding behavior.'),
                                                                                                                           ('P50', 10, 10, '2024-07-20', 'Breach of discipline', 1, 'Minor misconduct.'),
                                                                                                                           ('P51', 11, 11, '2024-07-21', 'Bonus', 4, 'Excellent behavior.'),
                                                                                                                           ('P52', 12, 12, '2024-07-22', 'Breach of discipline', 2, 'Moderate misconduct.'),
                                                                                                                           ('P53', 13, 13, '2024-07-23', 'Bonus', 3, 'Good behavior.'),
                                                                                                                           ('P54', 14, 14, '2024-07-24', 'Breach of discipline', 1, 'Minor misconduct.'),
                                                                                                                           ('P55', 15, 15, '2024-07-25', 'Bonus', 4, 'Very good behavior.'),
                                                                                                                           ('P56', 16, 16, '2024-07-26', 'Breach of discipline', 2, 'Moderate misconduct again.');



-- Thêm dữ liệu vào bảng crimes
INSERT INTO crimes (crime_name) VALUES
                                    ('Theft (Ăn trộm)'),
                                    ('Fraud (Gian lận)'),
                                    ('Assault (Tấn công)'),
                                    ('Robbery (Cướp)'),
                                    ('Drug Trafficking (Buôn bán ma túy)'),
                                    ('Murder (Giết người)'),
                                    ('Burglary (Trộm cắp)'),
                                    ('Kidnapping (Bắt cóc)'),
                                    ('Arson (Phóng hỏa)'),
                                    ('Embezzlement (Tham ô)'),
                                    ('Extortion (Tống tiền)'),
                                    ('Bribery (Hối lộ)'),
                                    ('Cybercrime (Tội phạm mạng)'),
                                    ('Manslaughter (Ngộ sát)'),
                                    ('Domestic Violence (Bạo lực gia đình)'),
                                    ('Terrorism (Khủng bố)'),
                                    ('Vandalism (Phá hoại tài sản công)'),
                                    ('Tax Evasion (Trốn thuế)'),
                                    ('Human Trafficking (Buôn người)'),
                                    ('Money Laundering (Rửa tiền)');

-- Thêm dữ liệu vào bảng disciplinary_measures

-- Thêm dữ liệu vào bảng commendations

-- Thêm dữ liệu vào bảng visit_log

-- Thêm dữ liệu vào bảng healths
