<?php defined('BASEPATH') OR exit('No direct script access allowed');

class Genba extends RESTSECURE_Controller
//class Genba extends REST_Controller
{
    protected $output = ['status' => 0,'content' => '', 'message' => ''];	
    public function __construct(){
        parent::__construct();		

	}
	
	public function kategori_get(){
		$section = $this->get('section');
		$this->output['status'] = 1;
		$this->output['content'] = $this->generateKategori($section);
		$this->response($this->output, 200);
	}
	
	public function section_get(){
		$this->load->model('section_model','section');
		$this->output['status'] = 1;
		$sections = $this->section->fields(['id','name'])->as_array()->get_many_by(['status' => 1]);
		foreach($sections as $k => $v){
			$sections[$k]['items'] = $this->generateKategori($v['id']);
		}
		$this->output['content'] = $sections;

		$this->response($this->output, 200);
	}

	private function generateKategori($section){
		$result = [];
		$this->load->model('concern_model','concern');
		$this->load->model('concern_item_model','concern_item');
		$concern_id = []; 
		$concern =  $this->concern->fields(['id','name'])->as_array()->get_many_by(['status' => 1, 'section_id' => $section]);		
		if(!empty($concern)){
			$concernId = arr2to1D($concern,'id');
				$concern_item =  arr2DToarrKey($this->concern_item->fields(['id','name','concern_id'])->as_array()->get_many_by(['status' => 1, 'concern_id' => $concernId]),'concern_id');		
				if(!empty($concern)){
					foreach($concern as $c){
						$concern_id = $c['id'];
						$c['items'] = isset($concern_item[$concern_id]) ? $concern_item[$concern_id] : [];
						array_push($result,$c);
					}
				}
		}
		
		return $result;
		/*return [
			[
				'id' => 1,
				'name' => 'Man',
				'items' => [
					[
						'id' => 1,
						'name' => 'kondisi satu',
						'images' => []
					],
					[
						'id' => 2,
						'name' => 'kondisi dua',
						'images' => []
					]
				]
			],
			[
				'id' => 2,
				'name' => 'Machine',
				'items' => [
					[
						'id' => 3,
						'name' => 'kondisi tiga',
						'images' => []
					],
					[
						'id' => 4,
						'name' => 'kondisi empat',
						'images' => []
					]
				]
			],
			[
				'id' => 3,
				'name' => 'Machine 32',
				'items' => [
					[
						'id' => 3,
						'name' => 'kondisi tiga',
						'images' => []
					]
				]
			],

		];*/
	}

	public function save_post()
    {   
		$userId = $this->decodedToken->id;
//		$userId = 1;
		$this->load->model('patrol_check_model','patrol');
		$area = $this->post('area_line');
		$part_no = $this->post('part_no');
		$foreman = $this->post('foreman');
		$shift =  $this->post('shift');
		$interval =  $this->post('interval');
		$concern_item =  $this->post('concern_item');
		$result =  $this->post('result');
		$keterangan =  $this->post('keterangan');
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
		foreach($concern_item as $i => $j){
			$tmp = [
				'area_line' => $area,
				'part_no' => $part_no,
				'foreman' => $foreman,
				'shift' => $shift,
				'interval' => $interval,
				'concern_item' => $j,
				'transaction_date' => date('Y-m-d'),
				'created_by' => $userId,
				'result' => isset($result[$i]) ? $result[$i] : '',				
				'description' => isset($keterangan[$i]) ? $keterangan[$i] : '',				
				'images' => isset($imageJenis[$j]) ? json_encode($imageJenis[$j]) : ''
			];
			
			$this->patrol->insert($tmp);
		}
        $this->response($this->output, 200);
    }
}
