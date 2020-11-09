<?php
//$image_file = base_url()."assets/images/feedmill_logo.png";
//['form_name' => 'IPG-FORM/PPIC-1/IC-6','version' => 3, 'date' => date('d.m.y'),'material_code' => 'RMI.WH.01.00000448','part_number' => 'MT227-05830','part_name' => 'Y187F-0.85-SN', 'arrive_date' => date('d/m/Y'), 'pack' => 'roll', 'references_number' => 'PO/2019/08/0001'],
echo '
	<table width="98%">
		<tbody>
            <tr>
                <td width="40%">
                    <table>
                        <tr><td colspan="2" style="font-size:90%">'.$data['form_name'].'</td></tr>
                        <tr><td style="font-size:90%">REV</td><td style="font-size:90%">: '.$data['version'].'</td></tr>
                        <tr><td style="font-size:90%">DATE</td><td style="font-size:90%">: '.$data['date'].'</td></tr>
                    </table>
                </td>
                <td width="40%">
                    <table align="center">
                        <tr><td>&nbsp;</td></tr>
                        <tr><td><h3>PENERIMAAN</h3></td></tr>
                        <tr><td><h3>MATERIAL</h3></td></tr>
                        
                    </table>
                </td>
                <td width="25%">'.$barcode.'</td>
            </tr>
		</tbody>
    </table>
    <div></div>
    <br />
    
    <table cellpadding="0" cellspacing="-5" width="99%">
        <tr>
            <td width="30%">
                <table class="table-border" cellpadding="2" >
                    <tr><td>ITEM NUMBER</td></tr>
                    <tr><td>PART NUMBER</td></tr>
                    <tr><td>PART NAME</td></tr>
                    <tr><td>TGL DATANG</td></tr>
                </table>
            </td>
            <td width="40%">
                <table class="table-border" cellpadding="2" >
                    <tr><td>'.$data['material_code'].'</td></tr>
                    <tr><td>'.$data['part_number'].'</td></tr>
                    <tr><td>'.$data['part_name'].'</td></tr>
                    <tr><td>'.$data['arrive_date'].'</td></tr>
                </table>
            </td>
            <td width="30%">
                <table class="table-border" cellpadding="0" >
                    <tr><td height="52">QC PASSED</td></tr>
                </table>
            </td>
        </tr>
    </table>	
    <br />
    <hr style="width:102%" />
';
?>
<style>
table{
    font-size : .76em;
}
.caption{
	font-size : .8em;
}
.table{
	width : 90%;
	margin : 6px;
}
.table-border td{
    font-weight:bold;
    text-align: center;
    border : .7px solid black;
	border-collapse : collapse;
}
</style>
