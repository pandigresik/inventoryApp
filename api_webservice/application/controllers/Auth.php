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

class Auth extends REST_Controller
{
    public function __construct(){
        parent::__construct();
        $this->load->helper(array('authorization','jwt'));
        $this->load->model('user_model','m_user');
    }
    /**
     * URL: http://localhost/CodeIgniter-JWT-Sample/auth/token
     * Method: GET
     */
    public function login_post()
    {   
        $username = $this->post('username');
        $password = $this->post('password');

        $tokenData = array();
        $output = array('status' => 0, 'content' => '', 'message' => '');
        $user = $this->m_user->fields(['id','username as nama','password','password_salt'])->get_by(['username' => $username, 'status' => 1]);
        if(!empty($user)){
            $encriptPass = md5($password.$user->password_salt);
//            log_message('error',$encriptPass);
//            log_message('error',$user->password); 
            if(strtolower($user->password) == strtolower($encriptPass)){
                $tokenData = ['user' => $user->nama, 'id' => $user->id];
                $output['user'] = $user;
                $output['token'] = AUTHORIZATION::generateToken($tokenData); 
                $output['status'] = 1;
            }else{
                $output['message'] = 'Username atau password salah';
            }
        }else{
            $output['message'] = 'Username tidak ditemukan';
        }                
        $this->response($output, 200);
    }
    
}
