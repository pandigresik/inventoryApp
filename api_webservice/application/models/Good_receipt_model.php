<?php

class Good_receipt_model extends MY_Model
{
    protected $_table = 'good_receipt';

    /**
    CREATE TABLE good_receipt
		(
			id int NOT NULL IDENTITY,
			kodebarang varchar(20) not null,
			no_label int not null,
			quantity int not null,
			reference_number varchar(20),
			tgl_datang date not null default getdate(),
			created_at datetime default getdate() ,
			updated_at datetime default getdate(),
			PRIMARY KEY (id)
		)
    
     */
}
