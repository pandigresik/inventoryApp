<?php defined('BASEPATH') OR exit('No direct script access allowed');

/*
 * Changes:
 * 1. This project contains .htaccess file for windows machine.
 *    Please update as per your requirements.
 *    Samples (Win/Linux): http://stackoverflow.com/questions/28525870/removing-index-php-from-url-in-codeigniter-on-mandriva
 *
 * 2. Change 'encryption_key' in application\config\config.php
 *    Link for encryption_key: http://jeffreybarke.net/tools/codeigniter-encryption-key-generator/
 * 
 * 3. Change 'jwt_key' in application\config\jwt.php
 *
 */

class Stock extends REST_Controller
{
    private $decodedToken;		
    public function __construct(){
        parent::__construct();
		$this->load->helper(array('authorization','jwt'));
		$this->load->model('movement_model','mm');
		$this->checkToken();
    }
    
    private function checkToken()
    {
        $headers = $this->input->request_headers();
        $result = false;
        if (array_key_exists('Authorization', $headers) && !empty($headers['Authorization'])) {
            $decodedToken = AUTHORIZATION::validateToken($headers['Authorization']);
            if ($decodedToken != false) {
                $this->decodedToken = $decodedToken;
                $result = true;
            }
        }

        if (!$result) {
            $this->response('Unauthorized', 401);

            return;
        }
    }	
    /**
     * URL: http://localhost/CodeIgniter-JWT-Sample/auth/token
     * Method: GET
     */
    public function savein_post()
    {   
	
	$datas = json_decode($this->post('listbarang'),1);
log_message('error',json_encode($this->decodedToken));
$output = ['status' => 1, 'message' => 'Barang berhasil ditambahkan'];
        $this->response($output, 200);
        return;
	$kodebarangs = array_column($datas,'kodebarang');	
	// periksa apakah sudah pernah discan atau belum
	$adaMovement = $this->mm->distinct()->fields(['kodebarang'])->get_many_by(['kodebarang' => $kodebarangs]);
	
	$sudahPernahScan = [];
	if(!empty($adaMovement)){
		/** periksa stocknya pastikan <= 0 */
		foreach($adaMovement as $_tmp){
			/** proses pengecekan stoknya nanti bisa disesuaikan */
			$jmlStock = $this->db->query('exec dbo.stockBarang \'\',\''.$_tmp->kodebarang.'\'')->row_array();

			//log_message('error',json_encode($jmlStock));
			if(!empty($jmlStock)){
				if($jmlStock['qty'] > 0){
					array_push($sudahPernahScan,$_tmp->kodebarang);
				}
			}
		}
		
		if(!empty($sudahPernahScan)){
			$output = ['status' => 0, 'message' => 'Kode barang \''.implode("','",$sudahPernahScan).'\' sudah pernah discan'];	
			$this->response($output, 200);	
		}
	}	

	$tglTransaksi = date('Y-m-d H:i:s');
	foreach($datas as $data){
		$data['tgl_transaksi'] = $tglTransaksi;
		$data['tipe'] = 'IN';
		$insert = $this->mm->insert($data);
	}
	
	if($insert){
		$output = ['status' => 1, 'message' => 'Barang berhasil ditambahkan'];
	}else{
		$output = ['status' => 0, 'message' => 'Barang gagal ditambahkan'];
	}
		
        $this->response($output, 200);
    }
    
    public function saveopname_post()
    {   
	
		$data = [
			'kodebarang' => $this->post('kodebarang'),
			'koderak' => $this->post('koderak'),
			'tgl_transaksi' => date('Y-m-d H:i:s'),
			'quantity' => $this->post('quantity'),
			'tipe' => 'SO'
		];
		//$insert = $this->mm->insert($data);
		$sql = "exec stockOpname '".$data['koderak']."','".$data['kodebarang']."','".$data['quantity']."'";
		$this->db->query($sql);
		/* harusnya nanti bisa ditambahkan kode untuk bisa mengoverride nilai stock sesungguhnya */
		
		$output = ['status' => 1, 'message' => 'Barang berhasil ditambahkan'];
	
	
        $this->response($output, 200);
    }
    
    
    public function saveout_post()
    {   
    		list($mr,$fg) = explode("__",$this->post('kodemr'));
		$data = [
		'kodebarang' => $this->post('kodebarang'),
		'koderak' => $this->post('koderak'),
		'reference_number' => $this->post('kodebatchorder'),
		'tgl_transaksi' => date('Y-m-d H:i:s'),
		'quantity' => $this->post('quantity'),
		'note' => $this->post('note'),
		'tipe' => 'OUT',
		'mr' => $mr == "Pilih MR" ? NULL : $mr,
		'fg' => $fg == "Pilih FG" ? NULL : $fg
	];
//	log_message('error', json_encode($data));
	$error = 0;
	/* cek stocknya dulu
//	$jmlStock = $this->db->query('exec dbo.stockBarang \'\',\''.$data['kodebarang'].'\'')->row_array();
*/
//	$jmlStock = $this->db->query('exec dbo.stockBarangRmi \'\',\''.$data['kodebarang'].'\',\''.$data['quantity'].'\'')->row_array();
	$jmlStock = ['status' => 0, 'message' => 'Tidak bisa dilakukan stock out, jumlah melebihi quantity '];
	if(empty($jmlStock)){
		$error++;
		$output = ['status' => 0, 'message' => $jmlStock['message']];
	}else{
		if(!$jmlStock['status']){
			$error++;
		}
		$output = ['status' => 0, 'message' => $jmlStock['message']];
	}
	
	if(!$error){
		$insert = $this->mm->insert($data);
		if($insert){
			$output = ['status' => 1, 'message' => 'Barang berhasil dikeluarkan'];
		}else{
			$output = ['status' => 0, 'message' => 'Barang gagal dikeluarkan'];
		}
	}
	

        $this->response($output, 200);
	}
	
	public function transfer_post()
    {   
		/** transfer out */
		$dataOut = [
		'kodebarang' => $this->post('kodebarang'),
		'koderak' => $this->post('koderakasal'),
		'tgl_transaksi' => date('Y-m-d H:i:s'),
		'quantity' => $this->post('quantity') * -1,
		'tipe' => 'TO'
	];
		/** transfer in */
		$dataIn = [
			'kodebarang' => $this->post('kodebarang'),
			'koderak' => $this->post('koderaktujuan'),
			'tgl_transaksi' => date('Y-m-d H:i:s'),
			'quantity' => $this->post('quantity'),
			'tipe' => 'TI'
		];
	$insert = $this->mm->insert($dataOut);
	$insert = $this->mm->insert($dataIn);
	if($insert){
		$output = ['status' => 1, 'message' => 'Barang berhasil dikeluarkan'];
	}else{
		$output = ['status' => 0, 'message' => 'Barang gagal dikeluarkan'];
	}

        $this->response($output, 200);
    }
/* parameter tgl_awal, tgl_akhir, rak, kodebarang*/
    public function history_get()
    {   
	$tglawal = $this->get('tglawal');
	$tglakhir = $this->get('tglakhir');
	$koderak = $this->get('koderak');
	$kodebarang = $this->get('kodebarang');
	/*$where = ['cast(tgl_transaksi as date) between \''.$tglawal.'\' and \''.$tglakhir.'\''];
	if(!empty($koderak)){
		$where['koderak'] = $koderak;
	}
	if(!empty($koderak)){
		$where['kodebarang'] = $kodebarang;
	}
	$detail = $this->mm->fields(['id','koderak','tipe as type','quantity as qty','tgl_transaksi'])->as_array()->get_many_by($where);
	*/
	$detail = $this->db->query('exec dbo.historyBarang \''.$tglawal.'\',\''.$tglakhir.'\',\''.$koderak.'\',\''.$kodebarang.'\'')->result_array();
	$output = [
		'status' => 1, 
		'message' => 'Data history barang',
		'content' => $detail
	];

        $this->response($output, 200);
    }	
/* parameter kodebarang  dan koderak (optional) menampilkan stock pada rak*/
    public function list_get()
    {
	$koderak = $this->get('koderak');
	$kodebarang = $this->get('kodebarang'); 
	$lot = $this->get('lot'); 
	if(!empty($lot) && !empty($kodebarang)){
		$lengthLotKodeBarang = 19;
		$kodebarang = substr($kodebarang,0,$lengthLotKodeBarang);
		$detail = $this->db->query('exec dbo.stockLotBarang \''.$koderak.'\',\''.$kodebarang.'\'')->result_array();	
	}else{
		$detail = $this->db->query('exec dbo.stockBarang \''.$koderak.'\',\''.$kodebarang.'\'')->result_array();	
	}

	$output = [
		'status' => 1, 
		'message' => 'Detail stok barang',
		'content' => $detail
	];

        $this->response($output, 200);
	}
	
	public function kodeRak_get()
    {
	$output = [
			'status' => 0, 
			'message' => 'Data tidak ditemukan',
			'content' => ''
		];	
	$kodebarang = $this->get('kodebarang'); 
//	$detail = $this->db->query('exec dbo.cariRakBarang \''.$kodebarang.'\'')->row_array();
	$detail = ['koderak' => 'A4', 'qty' => 55];
	if(!empty($detail)){
		$output['status'] = 1; 
		$output['content'] = $detail['koderak'];
		$output['qty'] = $detail['qty'];
	}
	

        $this->response($output, 200);
	}
	
	public function listMr_get()
    {

	$kodebarang = $this->get('kodebarang'); 
//	$detail = $this->db->query('exec dbo.getListMr \''.$kodebarang.'\'')->result_array();	
	$detail = [
	   ['mr' => 'MR7879878', 'fg' => 'KG8907878 - kjl'],
   	   ['mr' => 'MR7879879', 'fg' => 'KG8907877 - sjl'],
   	   ['mr' => 'MR7879880', 'fg' => 'KG8907879 - ljl'],
	];
	$output = [
		'status' => 1, 
		'message' => 'Detail stok barang',
		'content' => $detail
	];

        $this->response($output, 200);
	}	
	
	public function kategoriMr_get()
    {

//	$detail = $this->db->query('exec dbo.getKategoriMr')->result_array();	
	$detail = [
	   ['mr' => 'MR7879878'],
   	   ['mr' => 'MR7879879'],
   	   ['mr' => 'MR7879880'],
	];
	$output = [
		'status' => 1, 
		'message' => 'Detail stok barang',
		'content' => $detail
	];

        $this->response($output, 200);
	}
	
	public function itemRmi_get()
    {
	$rmi = $this->get('rmi'); 

//	$detail = $this->db->query('exec dbo.getDetailRmi \''.$rmi.'\'')->result_array();	
	$detail = [
	   ['rmi' => 'MR7879878', 'number' => 'A789', 'tanggal' => '2020-08-09'],
	   ['rmi' => 'MR7879878', 'number' => 'A789', 'tanggal' => '2020-08-09'],
	   ['rmi' => 'MR7879878', 'number' => 'A789', 'tanggal' => '2020-08-09'],
	];
	$output = [
		'status' => 1, 
		'message' => 'Detail stok barang',
		'content' => $detail
	];

        $this->response($output, 200);
	}
	public function stockRmi_get()
    {
	$rmi = $this->get('rmi'); 

//	$detail = $this->db->query('exec dbo.getStockRmi \''.$rmi.'\'')->result_array();	
	$detail = [
	   ['rmi' => 'MR7879877', 'number' => 'A789','name' => 'naman 787','namefg' => 'namafg', 'qty' => 90, 'qtyact' => 40],
		['rmi' => 'MR4879878', 'number' => 'A799','name' => 'nama 788','namefg' => 'namafg', 'qty' => 90, 'qtyact' => 40],
		['rmi' => 'MR7879879', 'number' => 'B789','name' => 'nama 799','namefg' => 'namafg', 'qty' => 90, 'qtyact' => 40]
	];

	$output = [
		'status' => 1, 
		'message' => 'Detail stok barang',
		'content' => $detail
	];

        $this->response($output, 200);
	}

	public function stockItemRmi_get()
    {

//	$detail = $this->db->query('exec dbo.getStockItemRmi')->result_array();	
	$detail = [
	   ['rmi' => 'MR7879878', 'number' => 'AB789','name' => 'namanya'],
	   ['rmi' => 'MR7879878', 'number' => 'AN789','name' => 'namanya'],
	   ['rmi' => 'MR7879878', 'number' => 'AC789','name' => 'namanya']
	];

	$output = [
		'status' => 1, 
		'message' => 'Detail stok barang',
		'content' => $detail
	];

        $this->response($output, 200);
	}	
		
	public function stockRakRmi_get()
    {
	$rmi = $this->get('rmi'); 

//	$detail = $this->db->query('exec dbo.getStockRakRmi \''.$rmi.'\'')->result_array();	
	$detail = [
	   ['rmi' => 'MR7879878', 'rak' => 'A789','qty' => '50'],
	   ['rmi' => 'MR7879878', 'rak' => 'A789','qty' => '50'],
	   ['rmi' => 'MR7879878', 'rak' => 'A789','qty' => '150'],
	];

	$output = [
		'status' => 1, 
		'message' => 'Detail stok barang',
		'content' => $detail
	];

        $this->response($output, 200);
	}	
	
}
