<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Generate extends MX_Controller {
	
	public function index()
	{
		$this->load->view('welcome_message');
	}
	
	public function qrcode(){
		ob_start();
	//	error_reporting(0);
		$this->load->library ( 'Pdf' );
		$pdf = new Pdf ( 'P', PDF_UNIT, 'A4', true, 'UTF-8', false );
		// set document information
		$pdf->SetCreator(PDF_CREATOR);
		$pdf->SetAuthor('Your Company');
		$pdf->SetTitle('Generate Barcode');
		$pdf->SetSubject('ID Barang');

		$pdf->SetDefaultMonospacedFont(PDF_FONT_MONOSPACED);

		// set margins
		$pdf->SetMargins(5,6,16,15);
		$pdf->SetPrintHeader ( false );
		$pdf->SetPrintFooter ( false );
		// set auto page breaks
		//$pdf->SetAutoPageBreak(TRUE, PDF_MARGIN_BOTTOM);

		// set image scale factor
		$pdf->setImageScale(PDF_IMAGE_SCALE_RATIO);

		$pdf->SetFont('helvetica', '', 6);
		$pdf->AddPage();

		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

		// set style for barcode
		$style = array(
		 'border' => true,
		    'vpadding' => 'auto',
		    'hpadding' => 'auto',
			'margin'  =>0,
            'position' => 'S',
		    'fgcolor' => array(0,0,0),
		    'bgcolor' => array(255,255,255),
		    'module_width' => 1, // width of a single module in points
		    'module_height' => 1 // height of a single module in points
		);

		$arr = array_column($this->db->query('exec listKodeBarang')->result_array(),'kodebarang');
		$count = 0;
		$xAwal = 15;
		$yAwal = 15;
		
		$x = $xAwal;
		$y = $yAwal;
		$width = 32;
		$height = 32;
		$margin = 15;
		$marginBaris = 23;
		$perbaris = 4;
		$barisperhalaman = 5;
		$indexbaris = 1;
		//$c = 0;
		//while($c < 100){
		foreach ($arr as $key => $value) {
			if($count == $perbaris){ //untuk menentukan batasan jumlah item per halaman
				$count = 0;
				$y = $pdf->getY() + $marginBaris;
				$x = $xAwal;
				//$pdf->ln();
				if($indexbaris >= $barisperhalaman){
					$x = $xAwal;
					$y = $yAwal;
					$pdf->AddPage();
					$indexbaris = 0;
				}
				$indexbaris++;
				
			}

			// $pdf->write2DBarcode($value, 'PDF417', $x*25+5, $y, 0, 9, $style, 'N');
            // aturannya seperti ini : write1DBarcode($code, $type, $x='', $y='', $w='', $h='', $xres='', $style='', $align='')
            // aturannya seperti ini : writeHTMLCell($w, $h, $x, $y, $html='', $border=0, $ln=0, $fill=false, $reseth=true, $align='', $autopadding=true)
            //$pdf->write2DBarcode($value, 'QRCODE,H', $x*30+5, $y+5, 200, 60, 1, $style, 'N');
            $pdf->write2DBarcode($value, 'QRCODE,H', $x, $y, $width, $height, $style, 'N');
            //$pdf->write1DBarcode($value, 'C39', $x*30+5, $y+5, 200, 60, 1, $style, 'N');
			$pdf->writeHTMLCell(($width + $margin), $height + ($marginBaris/2) , $x - ($margin/2), $y - ($marginBaris/4), '', 'LRTB', 1, 0, true, '', true);
            $pdf->SetFont('helvetica', 'B', 9);
			$pdf->Text($x, $y+$height+1, $value);
			
			//$pdf->ln();
			$x += ($width + $margin);
			$count++;
		}
	//	$c++;
	//}

		
		// ---------------------------------------------------------

		//Close and output PDF document
		$pdf->Output('barcode_tray.pdf', 'I');
		ob_end_flush();
	}

	public function label(){
		ini_set('max_execution_time',300);
		ob_start();
		error_reporting(0);
		$this->load->library ( 'Pdf' );
		$pdf = new Pdf ( 'P', PDF_UNIT, array(170,  270), true, 'UTF-8', false );
		//$pdf = new Pdf ( 'P', PDF_UNIT, 'A5', true, 'UTF-8', false );
		// set document information
		$pdf->SetCreator(PDF_CREATOR);
		$pdf->SetAuthor('Your Company');
		$pdf->SetTitle('Generate Barcode');
		$pdf->SetSubject('ID Barang');

		$pdf->SetDefaultMonospacedFont(PDF_FONT_MONOSPACED);

		// set margins
		$pdf->SetMargins(5,5,5,5);
		$pdf->SetPrintHeader ( false );
		$pdf->SetPrintFooter ( false );
		// set auto page breaks
		//$pdf->SetAutoPageBreak(TRUE, PDF_MARGIN_BOTTOM);

		// set image scale factor
		$pdf->setImageScale(PDF_IMAGE_SCALE_RATIO);

	 	$pdf->SetFont('helvetica', '', 10);
		$pdf->AddPage();
		$arr = $this->db->query('exec listKodeBarang')->result_array();
		/*$arr = [
			['form_name' => 'IPG-FORM/PPIC-1/IC-6','version' => 3, 'date' => date('d.m.y'),'material_code' => 'RMI.WH.01.00000448','part_number' => 'MT227-05830','part_name' => 'Y187F-0.85-SN', 'arrive_date' => date('d/m/Y'), 'pack' => 'roll', 'references_number' => 'PO/2019/08/0001'],
			['form_name' => 'IPG-FORM/PPIC-1/IC-6','version' => 3, 'date' => date('d.m.y'),'material_code' => 'RMI.WH.01.00000448','part_number' => 'MT227-05830','part_name' => 'Y187F-0.85-SN', 'arrive_date' => date('d/m/Y'), 'pack' => 'roll', 'references_number' => 'PO/2019/08/0001'],
			['form_name' => 'IPG-FORM/PPIC-1/IC-6','version' => 3, 'date' => date('d.m.y'),'material_code' => 'RMI.WH.01.00000448','part_number' => 'MT227-05830','part_name' => 'Y187F-0.85-SN', 'arrive_date' => date('d/m/Y'), 'pack' => 'roll', 'references_number' => 'PO/2019/08/0001'],
			['form_name' => 'IPG-FORM/PPIC-1/IC-6','version' => 3, 'date' => date('d.m.y'),'material_code' => 'RMI.WH.01.00000448','part_number' => 'MT227-05830','part_name' => 'Y187F-0.85-SN', 'arrive_date' => date('d/m/Y'), 'pack' => 'roll', 'references_number' => 'PO/2019/08/0001'],
			['form_name' => 'IPG-FORM/PPIC-1/IC-6','version' => 3, 'date' => date('d.m.y'),'material_code' => 'RMI.WH.01.00000448','part_number' => 'MT227-05830','part_name' => 'Y187F-0.85-SN', 'arrive_date' => date('d/m/Y'), 'pack' => 'roll', 'references_number' => 'PO/2019/08/0001'],
			['form_name' => 'IPG-FORM/PPIC-1/IC-6','version' => 3, 'date' => date('d.m.y'),'material_code' => 'RMI.WH.01.00000448','part_number' => 'MT227-05830','part_name' => 'Y187F-0.85-SN', 'arrive_date' => date('d/m/Y'), 'pack' => 'roll', 'references_number' => 'PO/2019/08/0001'],
			['form_name' => 'IPG-FORM/PPIC-1/IC-6','version' => 3, 'date' => date('d.m.y'),'material_code' => 'RMI.WH.01.00000448','part_number' => 'MT227-05830','part_name' => 'Y187F-0.85-SN', 'arrive_date' => date('d/m/Y'), 'pack' => 'roll', 'references_number' => 'PO/2019/08/0001'],
			['form_name' => 'IPG-FORM/PPIC-1/IC-6','version' => 3, 'date' => date('d.m.y'),'material_code' => 'RMI.WH.01.00000448','part_number' => 'MT227-05830','part_name' => 'Y187F-0.85-SN', 'arrive_date' => date('d/m/Y'), 'pack' => 'roll', 'references_number' => 'PO/2019/08/0001'],
		];*/
		//$breakLine = '%0A';
		$breakLine = PHP_EOL;
		$width = 77;
		$height = 35;
		$xAwal = $x =6;
		$yAwal = $y = 5;
		$marginBaris = 4;
		$margin = 3;
		$perbaris = 2;
		$count = 0;
		$indexbaris = 1;
		$barisperhalaman = 6;
		//$c = 0;
		//while($c < 100){
		foreach($arr as $k => $r){
			$dataQR = [$r['material_code'],$r['pack'],$r['arrive_date'],$r['references_number']];
			$params = $pdf->serializeTCPDFtagParameters ( array (
				implode($breakLine,$dataQR),
				'QRCODE,H',
				'',
				'',
				15,
				15
			) );
			$b = '<tcpdf method="write2DBarcode" params="' . $params . '" />';
			$html = $this->load->view ( 'label_pdf', array (
					'data' => $r,
					'barcode' => $b
			), true );
			//$pdf->AddPage ();
			//writeHTML ( $html, $ln=true, $fill=false, $reseth=false, $cell=false, $align=''
			if($count == $perbaris){ //untuk menentukan batasan jumlah item per halaman
				$count = 0;
				$y = $pdf->getY() + $height + $marginBaris;
				$x = $xAwal;
				//$pdf->ln();
				if($indexbaris >= $barisperhalaman){
					$x = $xAwal;
					$y = $yAwal;
					$pdf->AddPage();
					$indexbaris = 0;
				}
				$indexbaris++;
			}
			//writeHTMLCell($w, $h, $x, $y, $html='', $border=0, $ln=0, $fill=false, $reseth=true, $align='', $autopadding=true);
			//$pdf->writeHTML ( $html, true, false, true, true, 'L' );
			$pdf->writeHTMLCell($width, $height, $x, $y, $html, false, false, false, true, 'L' );
			$x += ($width + $margin);
			$count++;
		}
	//	$c++;
	//}

		//Close and output PDF document
		$pdf->Output('barcode_label.pdf', 'I');
		ob_end_flush();
	}
}

