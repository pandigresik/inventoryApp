CREATE TABLE users (
  id int NOT NULL auto_increment,
  username varchar(12) NOT NULL,
  email varchar(255) NOT NULL,
  password varchar(60) NOT NULL,
  password_salt varchar(255) DEFAULT NULL,
  status varchar(1) default '1',
  created_at timestamp default current_timestamp ,
  updated_at timestamp default current_timestamp,
  PRIMARY KEY (id)
) 
/** username admin, password = 'admin' */
INSERT INTO users (username,email,password,password_salt)
VALUES ('admin','admin@admin.co.id',md5(concat('admin',md5('randomtext'))),md5('randomtext'))

CREATE TABLE movement (
  id int NOT NULL auto_increment,
  kodebarang varchar(10) not null,
  koderak varchar(10) not null,
  quantity int not null,
  tipe varchar(3) not null,
  reference_number varchar(20),
  tgl_transaksi datetime not null,
  created_at timestamp default current_timestamp ,
  updated_at timestamp default current_timestamp,
  PRIMARY KEY (id)
) 

-- sqlserver
CREATE TABLE users (
  id int NOT NULL IDENTITY,
  username varchar(12) NOT NULL,
  email varchar(255) NOT NULL,
  password varchar(60) NOT NULL,
  password_salt varchar(255) DEFAULT NULL,
  status varchar(1) default '1',
  created_at datetime default getdate(),
  updated_at datetime default getdate(),
  PRIMARY KEY (id)
) 

-- md5('admin') = 21232F297A57A5A743894A0E4A801FC3
-- md5('admin21232F297A57A5A743894A0E4A801FC3') = 81f54086cc2fb1784cfea7e6897c840f
INSERT INTO users (username,email,password,password_salt)
VALUES ('admin','admin@admin.co.id','81f54086cc2fb1784cfea7e6897c840f','21232F297A57A5A743894A0E4A801FC3')


CREATE TABLE movement (
  id int NOT NULL IDENTITY,
  kodebarang varchar(10) not null,
  koderak varchar(10) not null,
  quantity int not null,
  tipe varchar(3) not null,
  reference_number varchar(20),
  tgl_transaksi datetime not null default getdate(),
  created_at datetime default getdate() ,
  updated_at datetime default getdate(),
  PRIMARY KEY (id)
) 

CREATE PROCEDURE [historyBarang]
	@tglawal varchar(20),
	@tglakhir varchar(20),
	@koderak varchar(20),
	@kodebarang varchar(20)
AS
BEGIN
	
	SET NOCOUNT ON;
	declare @sql varchar(max) = '
	SELECT id,koderak,kodebarang,tipe as type,quantity as qty,tgl_transaksi from dbo.movement
where cast(tgl_transaksi as date) between '''+@tglawal+''' and '''+@tglakhir+''''
	
	if(len(@koderak) > 0)
		BEGIN
			if(len(@kodebarang) > 0) set @sql = @sql + ' and koderak = '''+@koderak+''' and kodebarang = '''+@kodebarang+''''
			else set @sql = @sql + ' and koderak = '''+@koderak+''''
		END
	ELSE if(len(@kodebarang) > 0) set @sql = @sql + ' and kodebarang = '''+@kodebarang+''''	
	
	exec(@sql)
end;

create PROCEDURE [listKodeBarang]
AS
BEGIN
	
	SET NOCOUNT ON;
	select distinct kodebarang from movement
	
end;

CREATE PROCEDURE [stockBarang]
	@koderak varchar(20),
	@kodebarang varchar(20)
AS
BEGIN
	
	SET NOCOUNT ON;
	declare @sql varchar(max) = '
	SELECT koderak, kodebarang, sum(case when tipe = ''IN'' then quantity when tipe = ''OUT'' then quantity * -1 end) qty from dbo.movement
	'
	if(len(@koderak) > 0)
		BEGIN
			if(len(@kodebarang) > 0) set @sql = @sql + ' where koderak = '''+@koderak+''' and kodebarang = '''+@kodebarang+''''
			else set @sql = @sql + ' where koderak = '''+@koderak+''''
		END
	ELSE if(len(@kodebarang) > 0) set @sql = @sql + ' where kodebarang = '''+@kodebarang+''''	
	
	set @sql = @sql + ' group by koderak, kodebarang'
	exec(@sql)
end;

create PROCEDURE [stockOpname]
	@koderak varchar(20),
	@kodebarang varchar(20),
	@qty integer
AS
BEGIN
	
	SET NOCOUNT ON;
	insert into movement(koderak,kodebarang,quantity,tipe,tgl_transaksi) 
	values(@koderak,@kodebarang,@qty,'SO',getdate())
	
end;


alter PROCEDURE [listKodeBarang]
AS
BEGIN
	
	SET NOCOUNT ON
	select kodebarang as material_code,'IPG-FORM/PPIC-1/IC-6' as form_name,3 as version, cast(getdate() as varchar(12)) as [date],'MT227-05830' as part_number,'Y187F-0.85-SN' as part_name,cast(getdate() as varchar(12)) as arrive_date, 'roll' as pack,'PO/2019/08/0001' as references_number from movement
	
end;


CREATE TABLE [dbo].[section](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[name] [varchar](50) NOT NULL,
	[status] [char](1) NOT NULL default '1',
	[created_at] [datetime] NULL default getdate(),
	[updated_at] [datetime] NULL default getdate(),
	primary key (id)
) 

insert into section(name) values('Section 1'),('Section 2')
alter table concern add section_id int
update concern set section_id = 1
alter table concern add CONSTRAINT fk_concern_section_1 FOREIGN key (section_id) REFERENCES section(id)

CREATE PROCEDURE [dbo].[stockLotBarang]
	@koderak varchar(20),
	@kodebarang varchar(20)    
AS
BEGIN
	
	SET NOCOUNT ON;
	declare @sql varchar(max) = '
	SELECT koderak, kodebarang, sum(case when tipe = ''IN'' then quantity when tipe = ''OUT'' then quantity * -1 end) qty from dbo.movement
	'
	if(len(@koderak) > 0)
		BEGIN
			if(len(@kodebarang) > 0) set @sql = @sql + ' where koderak = '''+@koderak+''' and kodebarang like '''+@kodebarang+'%'''
			else set @sql = @sql + ' where koderak = '''+@koderak+''''
		END
	ELSE if(len(@kodebarang) > 0) set @sql = @sql + ' where kodebarang like '''+@kodebarang+'%'''	
	
	set @sql = @sql + ' group by koderak, kodebarang'
	exec(@sql)
end;
