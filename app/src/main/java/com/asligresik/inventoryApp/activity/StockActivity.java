package com.asligresik.inventoryApp.activity;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.asligresik.inventoryApp.R;
import com.asligresik.inventoryApp.fragment.HomeStockFragment;
import com.asligresik.inventoryApp.fragment.ScanFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockActivity extends BaseActivity {
    public static final String BARCODE_KEY = "BARCODE";
    private static final String TAG = "StockActivity";
    protected Fragment fragment;
    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;
    private FragmentTransaction fragmentTransaction;
    private String kodeBarang;
    private String kodeRak;
    private String kodeBatchOrder;
    private String kodeRakAsal;
    private String kodeRakTujuan;
    private Integer urutan = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        ButterKnife.bind(this);

        setKodeBarang("");
        setKodeRak("");
        setKodeBatchOrder("");
        if (mFragmentContainer != null) {
            if (savedInstanceState == null) {
                loadDefaultFragment();
            }
        }

    }

    public void loadDefaultFragment() {
        fragment = new HomeStockFragment();
        setFragment(fragment);
    }

    public void setFragment(Fragment fragment) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void addFragment(Fragment fragment) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void scan(String scan) {
        Bundle bundle = new Bundle();
        bundle.putString("scan", scan);
        Fragment fragmen = new ScanFragment();
        fragmen.setArguments(bundle);
        setFragment(fragmen);
    }

    public void scan(String scan, int urutan) {
        Bundle bundle = new Bundle();
        bundle.putString("scan", scan);
        bundle.putInt("urutan",urutan);
        Fragment fragmen = new ScanFragment();
        fragmen.setArguments(bundle);
        setFragment(fragmen);
    }
    public String getKodeBarang() {
        return kodeBarang;
    }

    public void setKodeBarang(String kodeBarang) {
        this.kodeBarang = kodeBarang;
    }

    public String getKodeRak() {
        return kodeRak;
    }

    public void setKodeRak(String kodeRak) {
        this.kodeRak = kodeRak;
    }

    public String getKodeBatchOrder() {
        return kodeBatchOrder;
    }

    public void setKodeBatchOrder(String kodeBatchOrder) {
        this.kodeBatchOrder = kodeBatchOrder;
    }

    public String getKodeRakAsal() {
        return kodeRakAsal;
    }

    public void setKodeRakAsal(String kodeRakAsal) {
        this.kodeRakAsal = kodeRakAsal;
    }

    public String getKodeRakTujuan() {
        return kodeRakTujuan;
    }

    public void setKodeRakTujuan(String kodeRakTujuan) {
        this.kodeRakTujuan = kodeRakTujuan;
    }

    public Integer getUrutan() {
        return urutan;
    }

    public void setUrutan(Integer urutan) {
        this.urutan = urutan;
    }
}
