package app.nh.com.appsoinsa.cls;

import java.util.List;

/**
 * Created by Dev21 on 28-11-16.
 */

public class Preobra {
    private String idPreObra;
    private String idApp;
    private String empresaId;
    private String bodegaId;
    private String estadoId;
    private String alias;
    private String direccion;
    private String latitud;
    private String longitud;
    private String fechaCreacion;
    private String fechaApk;
    private String descripcion;
    private List<Imagenes> imgs;

    public String getIdPreObra() {
        return idPreObra;
    }

    public void setIdPreObra(String idPreObra) {
        this.idPreObra = idPreObra;
    }

    public String getIdApp() {
        return idApp;
    }

    public void setIdApp(String idApp) {
        this.idApp = idApp;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getBodegaId() {
        return bodegaId;
    }

    public void setBodegaId(String bodegaId) {
        this.bodegaId = bodegaId;
    }

    public String getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(String estadoId) {
        this.estadoId = estadoId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaApk() {
        return fechaApk;
    }

    public void setFechaApk(String fechaApk) {
        this.fechaApk = fechaApk;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Imagenes> getImgs() {
        return imgs;
    }

    public void setImgs(List<Imagenes> imgs) {
        this.imgs = imgs;
    }
}
