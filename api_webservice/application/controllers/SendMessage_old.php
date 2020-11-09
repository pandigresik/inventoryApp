<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');
/** Generate by crud generator model pada 2020-04-14 14:36:23
*   method index, add, edit, delete, detail dapat dioverride jika ingin melakukan custom pada controller tertentu
*   Author afandi
*/

use GuzzleHttp\Client;
use GuzzleHttp\Psr7\Request;
use GuzzleHttp\Psr7\Response;
use GuzzleHttp\ClientInterface;
use GuzzleHttp\Promise\EachPromise;
use Psr\Http\Message\ResponseInterface;
use GuzzleHttp\Exception\RequestException;

class SendMessage extends MX_Controller {
    public $title = 'Data Message';
    private $successSend = 0;    
    private $result = ['status' => 0,'message' => ''];
    function __construct(){
        parent::__construct();        
        $this->load->model('message_model','message');
    }

    public function send()
    {   
        $where = 'send_status = 0 and send_date <= \''.date('Y-m-d H:i:s').'\'';                             
        $sendData = $this->message->as_array()->get_many_by($where);
        if(!empty($sendData)){
            $promiseWait = $this->build_request($sendData);            
            $promiseWait->promise()->wait();            
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
        $promises = (function () use ($lists, $config) {
        $header = [            
            'Accept' => 'application/json',
            'Authorization' => $config['token']
        ];	
        $client = new Client(['cookies' => true, 'headers' => $header]);                
        foreach ($lists as $list) {        	                        
            $dataPost = ['form_params' => [ 'phone' => $list['destination']]];
            if(!empty($list['image'])){
                $base64 = base64_encode(file_get_contents($list['image'])); 
                $dataPost['form_params']['file'] = $base64;
                $dataPost['form_params']['caption'] = $list['content'];
                $dataPost['form_params']['data'] = json_encode(['name' => $list['image']]);                

                $urlPost = $config['server'].'send-image-from-local';
            }else{
                $dataPost['form_params']['message'] = $list['content'];                
                $urlPost = $config['server'].'send-message';
            }
            $idMessage = $list['id'];
            
            //log_message('error', json_encode($dataPost));
            yield $client->requestAsync('POST',$urlPost,$dataPost)
                ->then(function (Response $response) use ($idMessage) {								
                    //log_message('error',$response->getBody()->getContents());
                    $html['data'] = $response->getBody()->getContents();     
                    $html['id'] = $idMessage;           
                    return $html;
                });
            }
        })();

        return new EachPromise($promises, [
            'concurrency' => 10,
            'fulfilled' => function (array $html) {
                log_message('error',json_encode($html['data']));
                $this->message->update($html['id'],['send_status' => 1, 'updated_at' => date('Y-m-d H:i:s')]);
                $this->successSend++;
            },
            'rejected' => function ($reason) {
                log_message('error','reject ' .$reason);
            },
        ]);        
    }
}

