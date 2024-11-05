package com.example.proyecto_shem.vista;

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.proyecto_shem.R
import com.google.android.material.navigation.NavigationView

class MenuPrincipalActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_principal)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        // Obtener el email del Intent
        val userEmail = intent.getStringExtra("user_email")

        // Cambiar el título o mensaje de bienvenida según el correo
        if (userEmail != null) {
            when (userEmail) {
                "i202030272@cibertec.edu.pe" -> supportActionBar?.title = "SUPERVISOR"
                "i202030261@cibertec.edu.pe",
                "i202030257@cibertec.edu.pe",
                "i202030288@cibertec.edu.pe" -> supportActionBar?.title = "PERSONAL DE SEGURIDAD"
                else -> supportActionBar?.title = "BIENVENIDO AL SISTEMA"
            }
        }

        drawer = findViewById(R.id.drawer_layout)

        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_oper,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_item_uno -> Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
            R.id.nav_item_dos_uno -> {

                // Navegar a la interfaz de Ingreso
                val intent = Intent(this, RegistroIngresoActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_item_dos_dos -> {

                // Navegar a la interfaz de Salida
                val intent = Intent(this, RegistroSalidaActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_item_tres -> {

                // Navegar a la interfaz de Permisos
                val intent = Intent(this, RegistroPermisoActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_item_cuatro -> {

                // Navegar a la interfaz de Permisos Salida
                val intent = Intent(this, RegistroPermisoSalidaActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_item_cuatro_uno -> {

                // Navegar a la interfaz de Ingreso
                val intent = Intent(this, ConsultaIngresoActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_item_cuatro_dos -> {

                // Navegar a la interfaz de Salida
                val intent = Intent(this, ConsultaSalidaActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_item_cinco -> {

                // Navegar a la interfaz de Acerca de Nosotros
                val intent = Intent(this, AcercaNosotrosActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_item_seis -> {

                // Navegar a la interfaz de Salir
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
