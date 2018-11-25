package org.dev4u.hv.guia7;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import modelo.AdaptadorCategoria;
import modelo.AdaptadorProducto;
import modelo.Categoria;
import modelo.DB;
import modelo.Producto;

public class ProductoList extends AppCompatActivity {

    private String IDCATEGORIA;
    private DB db;
    private AdaptadorProducto adaptadorProducto;
    private ListView listView;
    private TextView lblId_Prod;
    private EditText txtNombre_prod, txtDescripcion;
    private Button btnGuardar,btnEliminar;
    //lista de datos (producto)
    private ArrayList<Producto> lstProductos;
    //sirve para manejar la eliminacion
    private Producto producto_temp=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_list);

        IDCATEGORIA = getIntent().getExtras().getString("Id_Categoria");

        //inicializando los controles
        lblId_Prod               = (TextView) findViewById(R.id.lblIdProducto);
        txtNombre_prod           = (EditText) findViewById(R.id.txtProducto);
        txtDescripcion           = (EditText) findViewById(R.id.txtDescripcion);
        btnGuardar              = (Button)   findViewById(R.id.btnGuardar);
        btnEliminar             = (Button)   findViewById(R.id.btnEliminar);
        listView                = (ListView) findViewById(R.id.lstProducto);
        //inicializando lista y db
        db                      = new DB(this);
        lstProductos            = db.getArrayProducto(
                db.getCursorProducto(IDCATEGORIA)
        );
        if(lstProductos==null)//si no hay datos
            lstProductos = new ArrayList<>();
        adaptadorProducto      = new AdaptadorProducto(ProductoList.this,lstProductos);
        listView.setAdapter(adaptadorProducto);
        //listeners
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGuardarOnClick();
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnEliminarOnClick();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                seleccionar(lstProductos.get(position));
            }
        });

        limpiar();

    }

    private void btnGuardarOnClick(){
        Producto producto = new Producto(lblId_Prod.getText().toString(),txtNombre_prod.getText().toString(), txtDescripcion.getText().toString(), IDCATEGORIA);
        producto_temp = null;
        if(db.guardar_O_ActualizarProducto(producto)){
            Toast.makeText(this,"Se guardo el producto.",Toast.LENGTH_SHORT).show();
            //TODO limpiar los que existen y agregar los nuevos
            lstProductos.clear();
            lstProductos.addAll(db.getArrayProducto(
                    db.getCursorProducto(IDCATEGORIA)
            ));

            adaptadorProducto.notifyDataSetChanged();
            limpiar();
        }else{
            Toast.makeText(this,"Ocurrio un error al guardar",Toast.LENGTH_SHORT).show();
        }
    }
    private void btnEliminarOnClick(){
        if(producto_temp!=null){
            db.borrarProducto(producto_temp.getId_categoria());
            lstProductos.remove(producto_temp);
            adaptadorProducto.notifyDataSetChanged();
            producto_temp = null;
            Toast.makeText(this,"Se elimino producto",Toast.LENGTH_SHORT).show();
            limpiar();
        }
    }
    private void seleccionar(Producto producto){
        producto_temp = producto;
        lblId_Prod.setText(producto_temp.getId_categoria());
        txtDescripcion.setText(producto_temp.getNombre());
        txtNombre_prod.setText(producto_temp.getDescripcion());
    }
    private void limpiar(){
        lblId_Prod.setText(null);
        txtNombre_prod.setText(null);
        txtDescripcion.setText(null);
    }


}
