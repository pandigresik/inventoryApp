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

class GoodReceipt extends REST_Controller
{
    private $decodedToken;		
    public function __construct(){
        parent::__construct();
		$this->load->helper(array('authorization','jwt'));
//		$this->load->model('good_receipt','gm');
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
    		
	public function rmiSearch_get()
    {
	$rmi = $this->get('rmi'); 

//	$detail = $this->db->query('exec dbo.getStockRakRmi \''.$rmi.'\'')->result_array();	
	$detail = [
	   ['rmi' => 'RMI.WH.01.00000448', 'po' => ['PO/2019/08/0001','PO/2019/08/0002','PO/2019/08/0003']],
	   ['rmi' => 'RMI.WH.01.00000449', 'po' => ['PO/2019/08/0004','PO/2019/08/0005','PO/2019/08/0006']],
	   ['rmi' => 'RMI.WH.01.00000447', 'po' => ['PO/2019/08/0007','PO/2019/08/0008','PO/2019/08/0009']],
	];

	$output = [
		'status' => 1, 
		'message' => 'Detail stok barang',
		'content' => $detail
	];
	
        $this->response($output, 200);
	}	
	
	public function save_post()
    {   

		$data = [
		'rmi' => $this->post('rmi'),
		'po' => $this->post('po'),
		'tgl' => $this->post('tgl'),
		'qty' => $this->post('qty'),
		'quantity' => $this->post('jmllabel'),
	];
	$qrlabel = [];
	for($i = 0; $i < $data['quantity']; $i++){
		$nomer = "00001";
		$_data = $data;
		$_data['qty'] = $data['qty']/$data['quantity'];
		array_push($qrlabel,$this->generateLabel($_data,$nomer));
	}

	$output = ['status' => 1, 'message' => 'Barang berhasil dikeluarkan', 'content' => implode("",$qrlabel)];

        $this->response($output, 200);
    }
    
    private function generateLabel($data, $nomer){
        $dataQR = [$data['rmi'].$nomer, $data['qty'], $data['tgl'], $data['po']];
    	return "[C]<u><font size='big'>Penerimaan</font></u>\n
   		[C]".$nomer."\n
   		[C]<qrcode size='20'>".implode(PHP_EOL,$dataQR)."</qrcode>\n
   		[L]".$data['tgl']."\n
   		[L]".$data['po']."\n
   		[C]===================================
    	";
//                        "[C]<u><font size='big'>ORDER N°045</font></u>\n" +
//                        "[C]<font size='small'>" + format.format(new Date()) + "</font>\n" +
//                        "[L]\n" +
//                        "[C]================================\n" +
//                        "[L]\n" +
//                        "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99e\n" +
//                        "[L]  + Size : S\n" +
//                        "[L]\n" +
//                        "[L]<b>AWESOME HAT</b>[R]24.99e\n" +
//                        "[L]  + Size : 57/58\n" +
//                        "[L]\n" +
//                        "[C]--------------------------------\n" +
//                        "[R]TOTAL PRICE :[R]34.98e\n" +
//                        "[R]TAX :[R]4.23e\n" +
//                        "[L]\n" +
//                        "[C]================================\n" +
//                        "[L]\n" +
//                        "[L]<font size='tall'>Customer :</font>\n" +
//                        "[L]Raymond DUPONT\n" +
//                        "[L]5 rue des girafes\n" +
//                        "[L]31547 PERPETES\n" +
//                        "[L]Tel : +33801201456\n" +
//                        "[L]\n" +
//                        "[C]<barcode type='ean13' height='10'>831254784551</barcode>\n" +
//                        "[C]<qrcode size='20'>http://www.developpeur-web.dantsu.com/</qrcode>\n"
    }
	
}
