# Optimiza
Optimiza es la primera aplicación desarrollada para Android que permite al usuario obtener información completa de su dispositivo. Es capaz de mostrar todas las aplicaciones instaladas con sus permisos, presentar información detallada de la batería, mostrar todos los permisos que se encuentran ejecutandose en su dispositivo y poder eliminarlos para liberar memoria RAM, y además presenta información sobre las memorias en su dispositivo.

### Código
La primera vez que ejecuta el usuario la aplicación tendrá una intro formada por sliders que presentan un resumen del uso de la aplicación.

![slide1](https://cloud.githubusercontent.com/assets/6495659/8229515/0699bb40-15b8-11e5-8c7f-a169297425b4.png)
![slide3](https://cloud.githubusercontent.com/assets/6495659/8229514/069868da-15b8-11e5-96ff-c6d68be6ee4e.png)


Cuando comienza la ejecución de la aplicación necesitamos tener información sobre todas las apps instaladas en el dispositivo con la informacion de sus permisos, conseguido de la siguiente forma:

```
PackageManager pm = getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            ArrayList<AppsPermisos> apps = new ArrayList<>();

            for (ApplicationInfo applicationInfo : packages) {

                try {
                    PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);

                    //Get Permissions
                    String[] requestedPermissions = packageInfo.requestedPermissions;
                    if (requestedPermissions == null) continue;
                    AppsPermisos app = new AppsPermisos(pm.getApplicationIcon(packageInfo.packageName), pm.getApplicationLabel(applicationInfo).toString(), requestedPermissions, applicationInfo.packageName);
                    apps.add(app);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
```

La base de datos de las aplicaciones instaladas almacena: el nombre de la aplicación, el nombre del paquete y un flag llamado ignorada, que sirve para mostrar o no la aplicación en la lista.

```
    /**
     * Definimos constantes nombres de tablas
     */
    private static final String TABLE_NAME = "Permisos_APP";
    
    /**
     * Definimos constantes con el nombre de las columnas de la tabla permisos_app
     */
    public static final String COLUMNA_ID = "_id";
    public static final String COLUMNA_NOMBRE = "nombre";
    public static final String COLUMNA_PAQUETES = "packages";
    public static final String COLUMNA_IGNORADA = "ignorada";
    
    /**
     * Definimos constantes las querys para crear tablas
     */
    private static final String CREATE_TABLE_APP_PERMISOS = "CREATE TABLE " + TABLE_NAME + "(" + COLUMNA_ID + " " + TYPE_INTEGER + " PRIMARY KEY," + COLUMNA_NOMBRE + " " + TYPE_TEXT + " NOT NULL, " +
            COLUMNA_PAQUETES + " " + TYPE_TEXT + " NOT NULL," + COLUMNA_IGNORADA + " " + TYPE_INTEGER + ")";
```
            
Además se crea una base de datos dinámica, que descarga los datos de un archivo de texto con formato JSON guardado en Dropbox. Esta base de datos dinámica guarda el nombre del permiso y una breve descripción.


```
    /**
     * Definimos constantes nombres de tablas
     */
    private static final String TABLE_NAME_PERMISOS = "PERMISOS";
    
    /**
     * Definimos constantes con el nombre de las columnas de la tabla permisos
     */
    public static final String COLUMNA_PERMISO = "PERMISO";
    public static final String COLUMNA_DESCRIPCION = "DESCRIPCION";
    
    /**
     * Definimos constantes las querys para crear tablas
     */
    private static final String CREATE_TABLE_PERMISOS = "CREATE TABLE " + TABLE_NAME_PERMISOS + "(" + COLUMNA_ID + " " + TYPE_INTEGER + " PRIMARY KEY, " + COLUMNA_PERMISO + " " + TYPE_TEXT + " NOT NULL, " + COLUMNA_DESCRIPCION + " " + TYPE_TEXT + " NOT NULL)";
```

### Apps y permisos

La primera pestaña de la aplicación se llama Apps, presenta las aplicaciones instaladas en el dispositivo, su icono y el numero de permisos necesarios.

![apps_button](https://cloud.githubusercontent.com/assets/6495659/8229523/1f28bdd2-15b8-11e5-8243-a876311cd4c7.png)

Haciendo click en alguna de ellas, se presenta información detallada sobre dicha aplicación. Nombre de paquete, versión, fecha de instalación y actualización, enlace a GooglePlay y los permisos pedidos.

![apps_permisos](https://cloud.githubusercontent.com/assets/6495659/8229530/29f96d1a-15b8-11e5-9ab6-608e60ad4b28.png)

### Procesos

En la segunda pestaña podemos ver los procesos que estan activos en nuestro telefono con la memoria que requiere cada uno de ellos. La pestaña procesos, permite abrir la aplicación seleccionada, desinstalarla del dispositivo e incluso terminar con ella para liberar memoria ram.

![process](https://cloud.githubusercontent.com/assets/6495659/8229539/476e5f7c-15b8-11e5-93d4-2ad3492080b2.png)

![process_menu](https://cloud.githubusercontent.com/assets/6495659/8229540/4771dc9c-15b8-11e5-8b14-fcdaf73030ad.png)

![process_memoria](https://cloud.githubusercontent.com/assets/6495659/8229541/4772b0f4-15b8-11e5-92c2-042083a3c228.png)


### Memoria

Otra pestaña es la de memoria, en ella se muestra el porcentaje de memoria RAM, memoria interna y memoria externa(si existiese) ocupado. Asi como las memorias totales y la cantidad de memoria usada.

![memoria](https://cloud.githubusercontent.com/assets/6495659/8229552/5c42794c-15b8-11e5-97a5-1ee29b51374c.png)

### Batería

En la pestaña batería, se puede obtener información sobre el nivel de carga, el estado de carga, la temperatura de la bateria y el voltaje.

![battery](https://cloud.githubusercontent.com/assets/6495659/8229558/6a7950b2-15b8-11e5-92ab-111ab957a950.png)

### Drawer

Menú lateral con enlaces a distintas pestañas, con opción de compartir para enviar un mensaje de unión a la aplicación, y acerca de la aplicación.

![drawer](https://cloud.githubusercontent.com/assets/6495659/8229592/a7de3d78-15b8-11e5-8aee-9e8528a67c46.png)

![acerca](https://cloud.githubusercontent.com/assets/6495659/8229593/a7e026b0-15b8-11e5-91c0-4abf289337ec.png)


### Librerias

##### [MaterialViewPager](https://github.com/florent37/MaterialViewPager)

Tema principal de la aplicación. Navegación por pestañas con cardviews para las vistas.

##### [FloatingActionButton](https://github.com/makovkastar/FloatingActionButton)

Libreria para el boton flotante utilizado en la lista de procesos.

##### [MaterialDialog](https://github.com/drakeet/MaterialDialog)

Libreria para el uso de los dialog presentados en la aplicación.

##### [SmoothProgressBar](https://github.com/castorflex/SmoothProgressBar)

Progreso circular para la espera de la descarga de la descripcion del proceso.

##### [ArcProgress](https://github.com/lzyzsd/CircleProgress)

Arco con porcentaje para la cantidad de memoria utilizada.

##### [ScrollView](https://github.com/ksoichiro/Android-ObservableScrollView)

ScrollView para las pestañas de memoria y bateria.

##### [CircularMenu](https://github.com/oguzbilgener/CircularFloatingActionMenu)

Menu de botones flotantes para pestaña apps.

##### [FloatingActionButtonMenu](https://github.com/Clans/FloatingActionButton)

Complemento necesario para el anterior.

##### [AppIntro](https://github.com/PaoloRotolo/AppIntro)

Slider para intro por primera vez en la aplicación

##### Crashlytics
Sistema para saber donde la aplicación falla mientras un usuario se encuentra utilizandola. Presenta el sitio exacto del código donde salto una excepción no deseada. 

![crashlytics](https://cloud.githubusercontent.com/assets/6495659/8229689/69cdf220-15b9-11e5-8298-1238c5de39de.png)
### En desarrollo

- Mejora del calculo de memoria
- Calculo de mA consumidos por la bateria 
- Añadir API de Facebook para compartir post.
- Busqueda de apps por nombre

### Version
1.0

## Autor

Desarrollado por josedlpozo para Concurso Aplicaciones Android BQ-EESTEC.

## License 

>Copyright (C) 2015  

>José del Pozo 

>This program is free software: you can redistribute it and/or modify
>it under the terms of the GNU General Public License as published by
>the Free Software Foundation, either version 3 of the License, or
>(at your option) any later version.

>This program is distributed in the hope that it will be useful,
>but WITHOUT ANY WARRANTY; without even the implied warranty of
>MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
>GNU General Public License for more details.

>You should have received a copy of the GNU General Public License
>along with this program.  If not, see <http://www.gnu.org/licenses/>.
