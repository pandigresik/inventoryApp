<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');
/** Generate by crud generator model pada 2020-04-14 14:36:23
*   method index, add, edit, delete, detail dapat dioverride jika ingin melakukan custom pada controller tertentu
*   Author afandi
*/

use Curl\MultiCurl;

class SendMessageGroup extends MX_Controller {
    public $title = 'Data Message';
    private $successSend = 0;    
    private $result = ['status' => 0,'message' => ''];
    function __construct(){
        parent::__construct();        
        $this->load->model('group_message_model','message');        
    }

    public function send()
    {   
        $where = 'send_status = 0 and send_date <= \''.date('Y-m-d H:i:s').'\'';                             
        $sendData = $this->message->as_array()->get_many_by($where);
        /*$sendData = [
            ['content' => 'percobaan', 'destination' => '085733659400', 'id' => 9, 'image' => NULL]
        ];*/
        if(!empty($sendData)){
            $this->build_request($sendData);                        
        }                

        $this->result['status'] = 1;
        $this->result['message'] = $this->successSend.' sudah dikirim';
        echo json_encode($this->result);
    }

    private function build_request($lists){
        $config = array(
            'server'    => 'https://console.wablas.com/api/',				
            'token'     => '4JvNfPa5gigUZfe3Mqwqz5qFoTwqkpYqdFeXicGhPcAaLtNkM5vktZLk4GKiWQzt'
        );    
        $multiCurl = new MultiCurl();
        $header = [            
            'Accept' => 'application/json',
            'Authorization' => $config['token']
        ];
        $multiCurl->setHeaders($header);                	        
        //$groupId = '1588900398';
        foreach ($lists as $list) {        	                        
            $dataPost = ['form_params' => ['phone' => $list['destination'], 'groupId' => $list['group_id']]];
            if(!empty($list['image'])){
                $base64 = base64_encode(file_get_contents($list['image'])); 
                $dataPost['form_params']['file'] = $base64;
                $dataPost['form_params']['caption'] = $list['content'];
                $dataPost['form_params']['data'] = json_encode(['name' => $list['image']]);                

                $urlPost = $config['server'].'send-image-group-from-local';
            }else{
                $dataPost['form_params']['message'] = $list['content'];                
                $urlPost = $config['server'].'send-group';
            }            
            $multiCurl->addPost($urlPost,$list['id'],$dataPost['form_params']);                          
        }
        $multiCurl->success(function($instance) {              
            $headers = $instance->getOpt(CURLOPT_HTTPHEADER);            
            $idMessage = explode(': ',$headers[2]);
            $this->successSend++;
            //log_message('error',json_encode($idMessage));
            //log_message('error','idnya '.intval(trim($idMessage[1])));
            $this->message->update(intval(trim($idMessage[1])),['send_status' => 1, 'updated_at' => date('Y-m-d H:i:s')]);
            log_message('error','response '.json_encode($instance->response));            
        });       
        $multiCurl->error(function($instance) {            
            log_message('error',json_encode($instance->getOpt(CURLOPT_POSTFIELDS)));
            log_message('error',json_encode($instance->url));
            log_message('error',json_encode($instance->errorMessage));
        });       
        $multiCurl->start();
    }
}