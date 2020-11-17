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
		$this->load->model('good_receipt_model','grm');
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

//	$detail = $this->db->query('exec dbo.getGoodReceiptRmi \''.$rmi.'\'')->result_array();	
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
		'kodebarang' => $this->post('rmi'),
		'reference_number' => $this->post('po'),
		'tgl_datang' => $this->post('tgl'),
		'quantity' => intval($this->post('qty')),
		'qtylabel' => intval($this->post('jmllabel')),
	];

	$start = 0;
	$lastlabel = $this->grm->fields('no_label')->order_by('no_label','desc')->get_by(['tgl_datang' => date('Y-m-d'),'kodebarang' => $data['kodebarang']]);
    if(!empty($lastlabel)){
        $start = $lastlabel->no_label;
	}
    ++$start;
	$label = [];
	$qrlabel = [];
	for($i = 0; $i < $data['qtylabel']; $i++){
		$nomer = str_pad($start,4,"0",STR_PAD_LEFT);
		$_data = $data;
		$_data['quantity'] = ceil($data['quantity']/$data['qtylabel']);
		array_push($qrlabel,$this->generateLabel($_data,$nomer));
		array_push($label,$nomer);
        $_data['no_label'] = $start;
		unset($_data['qtylabel']);
        $this->grm->insert($_data);
        ++$start;
	}

	$output = ['status' => 1, 'message' => 'Barang berhasil diterima','label' => $label ,'content' => implode("",$qrlabel)];

        $this->response($output, 200);
    }
    
    private function generateLabel($data, $nomer){
        $dataQR = [$data['kodebarang'].'.'.$nomer, $data['quantity'], $data['tgl_datang'], $data['reference_number']];
    	return "[C]<u><font size='big'>Penerimaan</font></u>\n
   		[C]".$nomer."\n
   		[C]<qrcode size='20'>".implode('\n',$dataQR)."</qrcode>\n
   		[L]".$data['tgl_datang']."\n
   		[L]".$data['reference_number']."\n
   		[C]===================================\n
    		";
    }
	
}
