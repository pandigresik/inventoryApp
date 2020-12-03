package com.asligresik.inventoryApp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asligresik.inventoryApp.adapter.GoodReceiptSearchAdapter;
import com.asligresik.inventoryApp.model.PartNameGoodReceipt;
import com.asligresik.inventoryApp.model.ResponseSaveGoodReceipt;
import com.asligresik.inventoryApp.model.RmiGoodReceipt;
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.asligresik.inventoryApp.async.AsyncBluetoothEscPosPrint;
import com.asligresik.inventoryApp.async.AsyncEscPosPrinter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.google.gson.internal.LinkedTreeMap;

import com.asligresik.inventoryApp.R;
import com.zj.btsdk.BluetoothService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrintLabelActivity extends BaseActivity
        implements EasyPermissions.PermissionCallbacks, BluetoothHandler.HandlerInterface {
    public static final int RC_BLUETOOTH = 0;
    public static final int RC_CONNECT_DEVICE = 1;
    public static final int RC_ENABLE_BLUETOOTH = 2;

    private final String TAG = PrintLabelActivity.class.getSimpleName();
    private BluetoothService mService = null;
    private boolean isPrinterReady = false;
    private Context mContext;
    private ArrayAdapter<String> poAdapter;
    private ArrayAdapter<String> rmiAdapter;
    private String printText;
    private List<String> sequenceLabel = new ArrayList<String>();

    ProgressDialog loading;

    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.etTglDatang)
    EditText etTglDatang;
    @BindView(R.id.acRmi)
    AutoCompleteTextView acRmi;
    @BindView(R.id.acPoNumber)
    AutoCompleteTextView acPoNumber;
    @BindView(R.id.etLabelQuantity)
    EditText etLabelQuantity;
    @BindView(R.id.etQuantity)
    EditText etQuantity;
    @BindView(R.id.acPartName)
    DelayAutoCompleteTextView acPartName;
    @BindView(R.id.etPartNumber)
    EditText etPartNumber;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar loadingIndicator;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.btnPrint)
    Button btnPrint;
    @BindView(R.id.btnSavePrint)
    Button btnSavePrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_label);
        mContext = this;
        ButterKnife.bind(this);
        setupBluetooth();
        poAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                new ArrayList<String>());
        rmiAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                new ArrayList<String>());
        GoodReceiptSearchAdapter pnAdapter = new GoodReceiptSearchAdapter(mContext);
        acPoNumber.setAdapter(poAdapter);
        acRmi.setAdapter(rmiAdapter);
        acPartName.setAdapter(pnAdapter);
        acPartName.setThreshold(4);
        acPartName.setLoadingIndicator(loadingIndicator);
        acPartName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pnSelected = (String) parent.getItemAtPosition(position);
                PartNameGoodReceipt partNameGoodReceiptSelected = pnAdapter.getPartNameList().get(pnSelected);
                Log.d("list rmi", partNameGoodReceiptSelected.toString());
                List<String> listRmi = partNameGoodReceiptSelected.getKeyRmi();
                Log.d("list rmi", listRmi.toString());
                poAdapter.clear();
                poAdapter.notifyDataSetChanged();
                rmiAdapter.clear();
                rmiAdapter.addAll(listRmi);
                rmiAdapter.notifyDataSetChanged();
                // acPartName.setText(pnSelected.getPartname());
            }
        });
        acRmi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String rmi = (String) parent.getItemAtPosition(position);
                String partName = acPartName.getText().toString();
                LinkedTreeMap<String, RmiGoodReceipt> rmiMap = pnAdapter.getListRmi(partName);
                poAdapter.clear();
                poAdapter.addAll(rmiMap.get(rmi).getPo());
                poAdapter.notifyDataSetChanged();
                etPartNumber.setText(rmiMap.get(rmi).getPartnumber());
                // acRmi.setText(rmi.getRmi());
            }
        });

        printText = "[C]<qrcode size='20'>http://www.developpeur-web.dantsu.com/</qrcode>\n"
                + "[C]<qrcode size='20'>RMI.909089898&2020-09-09&87</qrcode>\n";
    }

    @OnClick(R.id.btnSave)
    public void simpan() {
        saveGoodReceipt(false);
    }

    @OnClick(R.id.btnSavePrint)
    public void simpanCetak() {
        saveGoodReceipt(true);
    }

    @OnClick(R.id.btnPrint)
    public void cetakLabelText() {
        String printText = getPrintedText();
        new AsyncBluetoothEscPosPrint(this).execute(this.getAsyncEscPosPrinter(null, printText));
    }

    private String getPrintedText() {
        String labelQR = "";
        if (!sequenceLabel.isEmpty()) {
            String rmi = acRmi.getText().toString();
            String partname = acPartName.getText().toString();
            String partnumber = etPartNumber.getText().toString();
            String po = acPoNumber.getText().toString();
            int jmllabel = Integer.parseInt(etLabelQuantity.getText().toString());
            String tgl = etTglDatang.getText().toString();
            // int qty = Integer.parseInt(etQuantity.getText().toString()) / jmllabel;
            int qty = Integer.parseInt(etQuantity.getText().toString());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("rmi", rmi);
            hashMap.put("partname", partname);
            hashMap.put("partnumber", partnumber);
            hashMap.put("po", po);
            hashMap.put("tgl", tgl);
            hashMap.put("qty", String.valueOf(qty));
            for (String label : sequenceLabel) {
                labelQR += generateQRLabel(label, hashMap);
            }
        }
        return labelQR;
    }

    private String generateQRLabel(String label, HashMap<String, String> hashMap) {
        String tgl = hashMap.get("tgl").replace("-", ".").substring(2);
        String result = "[C]<font size='tall'>LABEL MATERIAL</font>\n" + "[L]" + hashMap.get("tgl").substring(0, 7)
                + "[C]" + label + "\n" + "[C]<qrcode size='18'>" + hashMap.get("rmi") + "." + tgl + "." + label + "&"
                + hashMap.get("tgl") + "&" + hashMap.get("qty") + "&" + hashMap.get("po") + "</qrcode>\n"
                + "[L]Tanggal :" + hashMap.get("tgl") + "\n" + "[L]Quantity :" + hashMap.get("qty") + "\n" + "[L]<b>"
                + hashMap.get("rmi") + "</b>\n" + "[L]<b>" + hashMap.get("partnumber") + "</b>\n" + "[L]<b>"
                + hashMap.get("partname") + "</b>\n" + "[C]__________________________\n";
        return result;
    }

    @OnClick(R.id.btnDirect)
    public void cetakLabel() {
        new AsyncBluetoothEscPosPrint(this).execute(this.getAsyncEscPosPrinter(null, this.printText));
    }

    @OnClick(R.id.tglDatangBtn)
    public void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int Year, Month, Day;
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                String monthStr = String.format("%02d", (month + 1));
                String dateStr = String.format("%02d", date);
                etTglDatang.setText("" + year + "-" + monthStr + "-" + dateStr);
            }
        }, Year, Month, Day);

        datePickerDialog.show();
    }

    @AfterPermissionGranted(RC_BLUETOOTH)
    private void setupBluetooth() {
        String[] params = { Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN };
        if (!EasyPermissions.hasPermissions(this, params)) {
            EasyPermissions.requestPermissions(this, "You need bluetooth permission", RC_BLUETOOTH, params);
            return;
        }
        mService = new BluetoothService(this, new BluetoothHandler(this));
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        // TODO: 10/11/17 do something
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // TODO: 10/11/17 do something
    }

    @Override
    public void onDeviceConnected() {
        isPrinterReady = true;
        tvStatus.setText("Terhubung dengan perangkat");
    }

    @Override
    public void onDeviceConnecting() {
        tvStatus.setText("Sedang menghubungkan...");
    }

    @Override
    public void onDeviceConnectionLost() {
        isPrinterReady = false;
        tvStatus.setText("Koneksi perangkat terputus");
    }

    @Override
    public void onDeviceUnableToConnect() {
        tvStatus.setText("Tidak dapat terhubung ke perangkat");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_ENABLE_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "onActivityResult: bluetooth aktif");
                } else
                    Log.i(TAG, "onActivityResult: bluetooth harus aktif untuk menggunakan fitur ini");
                break;
            case RC_CONNECT_DEVICE:
                if (resultCode == RESULT_OK) {
                    String address = data.getExtras().getString(DeviceActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice mDevice = mService.getDevByMac(address);
                    mService.connect(mDevice);
                }
                break;
        }
    }
    /*
     * =============================================================================
     * ================= ======================================BLUETOOTH
     * PART============================================
     * =============================================================================
     * =================
     */

    public static final int PERMISSION_BLUETOOTH = 1;

    public void saveGoodReceipt(Boolean autoPrint) {
        if (!mService.isAvailable()) {
            return;
        }
        if (isPrinterReady) {
            String rmi = acRmi.getText().toString();
            String po = acPoNumber.getText().toString();
            String jmllabel = etLabelQuantity.getText().toString();
            String tgl = etTglDatang.getText().toString();
            String qty = etQuantity.getText().toString();
            loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
            getmApiService().saveGoodReceipt(rmi, po, tgl, jmllabel, qty)
                    .enqueue(new Callback<ResponseSaveGoodReceipt>() {
                        @Override
                        public void onResponse(Call<ResponseSaveGoodReceipt> call,
                                Response<ResponseSaveGoodReceipt> response) {
                            if (response.isSuccessful()) {
                                loading.dismiss();
                                String message = response.body().getMessage();
                                Integer status = response.body().getStatus();
                                if (status == 1) {
                                    String _printText = response.body().getPrintText();
                                    sequenceLabel = response.body().getLabel();
                                    loading.dismiss();
                                    if (autoPrint) {
                                        btnPrint.performClick();
                                    }
                                }
                            } else {
                                loading.dismiss();
                                Toast.makeText(mContext, "Gagal menyimpan good receipt", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseSaveGoodReceipt> call, Throwable t) {
                            loading.dismiss();
                            Toast.makeText(mContext, "Koneksi bermasalah ketika menyimpan good receipt",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            // this.printIt(BluetoothPrintersConnections.selectFirstPaired());
            // new
            // AsyncBluetoothEscPosPrint(this).execute(this.getAsyncEscPosPrinter(null));
        } else {
            if (mService.isBTopen())
                startActivityForResult(new Intent(this, DeviceActivity.class), RC_CONNECT_DEVICE);
            else
                requestBluetooth();
        }

    }

    private void requestBluetooth() {
        if (mService != null) {
            if (!mService.isBTopen()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, RC_ENABLE_BLUETOOTH);
            }
        }
    }

    /*
     * =============================================================================
     * ================= ===================================ESC/POS PRINTER
     * PART=========================================
     * =============================================================================
     * =================
     */

    /**
     * Synchronous printing
     */
    @SuppressLint("SimpleDateFormat")
    public void printIt(DeviceConnection printerConnection) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss");
            EscPosPrinter printer = new EscPosPrinter(printerConnection, 203, 48f, 32);
            printer.printFormattedText("[C]<img>"
                    + PrinterTextParserImg.bitmapToHexadecimalString(printer,
                            this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.logo,
                                    DisplayMetrics.DENSITY_MEDIUM))
                    + "</img>\n" + "[L]\n" + "[C]<u><font size='big'>ORDER N°045</font></u>\n"
                    + "[C]<font size='small'>" + format.format(new Date()) + "</font>\n" + "[L]\n"
                    + "[C]================================\n" + "[L]\n" + "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99e\n"
                    + "[L]  + Size : S\n" + "[L]\n" + "[L]<b>AWESOME HAT</b>[R]24.99e\n" + "[L]  + Size : 57/58\n"
                    + "[L]\n" + "[C]--------------------------------\n" + "[R]TOTAL PRICE :[R]34.98e\n"
                    + "[R]TAX :[R]4.23e\n" + "[L]\n" + "[C]================================\n" + "[L]\n"
                    + "[L]<font size='tall'>Customer :</font>\n" + "[L]Raymond DUPONT\n" + "[L]5 rue des girafes\n"
                    + "[L]31547 PERPETES\n" + "[L]Tel : +33801201456\n" + "[L]\n"
                    + "[C]<barcode type='ean13' height='10'>831254784551</barcode>\n"
                    + "[C]<qrcode size='20'>http://www.developpeur-web.dantsu.com/</qrcode>");
        } catch (EscPosConnectionException e) {
            e.printStackTrace();
            new AlertDialog.Builder(this).setTitle("Broken connection").setMessage(e.getMessage()).show();
        } catch (EscPosParserException e) {
            e.printStackTrace();
            new AlertDialog.Builder(this).setTitle("Invalid formatted text").setMessage(e.getMessage()).show();
        } catch (EscPosEncodingException e) {
            e.printStackTrace();
            new AlertDialog.Builder(this).setTitle("Bad selected encoding").setMessage(e.getMessage()).show();
        } catch (EscPosBarcodeException e) {
            e.printStackTrace();
            new AlertDialog.Builder(this).setTitle("Invalid barcode").setMessage(e.getMessage()).show();
        }
    }

    /**
     * Asynchronous printing
     */
    @SuppressLint("SimpleDateFormat")
    public AsyncEscPosPrinter getAsyncEscPosPrinter(DeviceConnection printerConnection, String printText) {
        SimpleDateFormat format = new SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss");
        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 300, 48f, 32);
        // Toast.makeText(mContext,"Siap Cetak "+printText, Toast.LENGTH_SHORT).show();
        return printer.setTextToPrint(printText);
    }
}
