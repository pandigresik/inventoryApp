<?php defined('BASEPATH') OR exit('No direct script access allowed');

class Inspection extends REST_Controller
{
    protected $output = ['status' => 0,'content' => '', 'message' => ''];	
    public function __construct(){
        parent::__construct();
		//$this->load->helper(array('authorization','jwt'));
		$this->load->model('inspection_model','im');
    }
    /**
     * URL: http://localhost/CodeIgniter-JWT-Sample/auth/token
     * Method: GET
     */
    public function save_post()
    {   
		$area = $this->post('area');
		$workorder = $this->post('workorder');
		$machine = $this->post('machine');
		$operator = $this->post('operator');
		$jenis =  $this->post('jenis');
		$keterangan =  $this->post('keterangan');
		$qty =  $this->post('qty');
		$imageJenis = [];
		if(!empty($_FILES)){
			foreach($_FILES as $index => $f){
				$uploadDir = 'uploads/';
				$newFileName = $uploadDir.$f['name'];
				move_uploaded_file($f['tmp_name'],$newFileName);
				
				$indexJenisTmp = explode('_',$index);
				$indexJenis = $indexJenisTmp[0];
				if(!isset($imageJenis[$indexJenis])){
					$imageJenis[$indexJenis] = [];
				}
				array_push($imageJenis[$indexJenis],$newFileName);
			}
		}
		foreach($jenis as $i => $j){
			$tmp = [
				'area' => $area,
				'workorder' => $workorder,
				'machine' => $machine,
				'operator' => $operator,
				'jenis' => $j,
				'keterangan' => isset($keterangan[$i]) ? $keterangan[$i] : '',
				'qty' => isset($qty[$i]) ? $qty[$i] : '',
				'gambar' => isset($imageJenis[$j]) ? json_encode($imageJenis[$j]) : ''
			];
			$this->im->insert($tmp);
		}
        $this->response($this->output, 200);
    }
    
    public function area_get(){
	$this->output['status'] = 1;
	$this->output['preassy'] = $this->db->query('exec dbo.listKodeArea')->result_array();
	$this->output['incoming'] = $this->db->query('exec dbo.listKodeAreaIncoming')->result_array();
	$this->output['assy'] = $this->db->query('exec dbo.listKodeAreaAssy')->result_array();
	$this->response($this->output, 200);
    }

    public function jenisng_get(){
	$this->output['status'] = 1;
	$this->output['preassy'] = $this->db->query('exec dbo.listJenisNG')->result_array();
	$this->output['incoming'] = $this->db->query('exec dbo.listJenisNGIncoming')->result_array();
	$this->output['assy'] = $this->db->query('exec dbo.listJenisNGAssy')->result_array();
	$this->response($this->output, 200);
    }		

    public function finishgood_get(){
	$wo = $this->get('workorder');
	$content = $this->db->query('exec dbo.cariFinishGood \''.$wo.'\'')->row_array();
	$this->output['status'] = 1;
	$this->output['content'] = $content['result'];
	$this->response($this->output, 200);
    }

    public function history_get()
    {   
	$result = [];
	$tglawal = $this->get('tglawal');
	$tglakhir = $this->get('tglakhir');
	$area = $this->get('area');
	$workorder = $this->get('workorder');
	$where = ['cast(created_at as date) between \''.$tglawal.'\' and \''.$tglakhir.'\''];
	if(!empty($area)){
		$where['area'] = $area;
	}
	if(!empty($workorder)){
		$where['workorder'] = $workorder;
	}
	$detail = $this->im->fields(['area','workorder','jenis','created_at','keterangan','qty','gambar'])->as_array()->get_many_by($where);
//log_message('error',json_encode($detail));
	if(!empty($detail)){
		foreach($detail as $d){
			if(!empty($d['gambar'])){
				$tmpGambar = [];
				
				foreach(json_decode($d['gambar'],1) as $gmb){
					array_push($tmpGambar,site_url($gmb));
				}
				$d['gambar'] = $tmpGambar;
			}else{
				$d['gambar'] = [];			
			}
			
			array_push($result,$d);			
			
		}	
	}
	//log_message('error',json_encode($result));

	/*
	$detail = $this->db->query('exec dbo.historyBarang \''.$tglawal.'\',\''.$tglakhir.'\',\''.$koderak.'\',\''.$kodebarang.'\'')->result_array();
*/
	$output = [
		'status' => 1, 
		'message' => 'Data history barang',
		'content' => $result
	];

        $this->response($output, 200);
    }	

}
