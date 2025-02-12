package com.albasil.finalprojectkotlinbootcamp.UI

import android.annotation.SuppressLint
import android.app.Activity
import android.app.UiModeManager.MODE_NIGHT_YES
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.core.app.ActivityCompat.recreate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.albasil.finalprojectkotlinbootcamp.MainActivity
import com.albasil.finalprojectkotlinbootcamp.R
import com.albasil.finalprojectkotlinbootcamp.ViewModels.ProfileViewModel
import com.albasil.finalprojectkotlinbootcamp.ViewModels.SettingsViewModel
import com.albasil.finalprojectkotlinbootcamp.databinding.FragmentSettingBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.change_langauge.view.*
import kotlinx.android.synthetic.main.change_password_bottom_sheet.view.*
import kotlinx.android.synthetic.main.help_and_support.view.*
import java.util.*

@Suppress("DEPRECATION")



class Setting : Fragment() {
    lateinit var binding: FragmentSettingBinding
    private lateinit var settingsViewModel: SettingsViewModel


    private val preferencesFeather by lazy {
         this.requireActivity().getSharedPreferences("preference", Context.MODE_PRIVATE)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater,container,false)

        return binding.root
    }
    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        binding.switchDarkMode.isChecked =preferencesFeather.getBoolean("MODE",false)


        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            } else {

                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            }
            preferencesFeather.edit().putBoolean("MODE",isChecked).apply()
        }




            binding.buttonLogOutXml.setOnClickListener {
                signOut()
            }

            binding.tvChangePasswordXml.setOnClickListener {
                dialogChangePassword(view)

            }


            binding.aboutUsId.setOnClickListener {
                aboutUs()
            }

            binding.helpAndSupportId.setOnClickListener {
                support()
            }
            binding.changeLanguagId.setOnClickListener {
                dialogChangeLanguage()
            }
        }

    private fun signOut() {
        preferencesFeather.getString("EMAIL", "")
        preferencesFeather.getString("PASSWORD", "")
        val editor: SharedPreferences.Editor = preferencesFeather.edit()
        editor.clear()
        editor.apply()
        findNavController().navigate(R.id.action_setting_to_sign_in)
        FirebaseAuth.getInstance().signOut()
    }


    //------------------------------------------------------------------
   private fun dialogChangeLanguage() {
        val view: View = layoutInflater.inflate(R.layout.change_langauge, null)
        val builder = BottomSheetDialog(requireView().context!!)
        builder.setTitle(getString(R.string.change_language))
        val btnChangeLanguage = view.btnChangeLanguage
       val radioGroup = view.radioGroup
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedLanguage:RadioButton=view.findViewById(checkedId)
            btnChangeLanguage.setOnClickListener {

                if (selectedLanguage.text.toString()=="عربي"){
                    setLocaleFeather("ar")

                }else if (selectedLanguage.text.toString()=="English"){
                    setLocaleFeather("en")

                }
            }
        }
        builder.setContentView(view)
        builder.show()
    }

    private fun setLocaleFeather(localeName: String) {
        val locale =Locale(localeName)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context?.resources?.updateConfiguration(config, requireContext().resources.displayMetrics)
        val editor: SharedPreferences.Editor = preferencesFeather.edit()
        editor.putString("preference", "${locale.toString()}")
        editor.apply()

        recreate(context as Activity)
    }


        //--------------------------------------------------------------------------
        fun dialogChangePassword(view: View) {

            val view: View = layoutInflater.inflate(R.layout.change_password_bottom_sheet, null)
            val builder = BottomSheetDialog(requireView().context!!)
            val oldPassword = view.etOldPassword_xml
            val etNewPassword = view.etNewPassword_xml
            val confirmNewPassword = view.etConfirmNewPassword_xml
            val btnChangePasswor = view.buttonChangePassword_xml

            builder.setContentView(view)

            btnChangePasswor.setOnClickListener {

                when {
                    TextUtils.isEmpty(oldPassword.text.toString().trim { it <= ' ' }) -> {
                        //  val toastMessageEmail: String = this.getResources().getString(R.string.please_enter_email)
                        val enter_old_password: String = this.getResources().getString(R.string.enter_old_password)

                        view.tvOldPassword_xml.helperText=enter_old_password
                    }
                    TextUtils.isEmpty(etNewPassword.text.toString().trim { it <= ' ' }) -> {
                          val enter_new_password: String = this.getResources().getString(R.string.enter_new_password)
                        view.tvNewPassword_xml.helperText=enter_new_password
                    }
                    TextUtils.isEmpty(confirmNewPassword.text.toString().trim { it <= ' ' }) -> {

                        val tvConfirmNewPassword_xml: String = this.getResources().getString(R.string.enter_new_password)

                        view.tvConfirmNewPassword_xml.helperText=tvConfirmNewPassword_xml
                    }
                    else -> {

                        if (etNewPassword.text.toString().equals(confirmNewPassword.text.toString())){
                            settingsViewModel.changePassword(
                                "${oldPassword.text.toString()}",
                                "${etNewPassword.text.toString()}",
                                "${confirmNewPassword.text.toString()}",view
                            )
                        }else{
                            Toast.makeText(context, "New Password is not equals Confirm New Password.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }


            }
            builder.show()

        }

        //--------------------------------------------------------------------------
        private fun aboutUs() {
            val view: View = layoutInflater.inflate(R.layout.about_us, null)
            val builder = BottomSheetDialog(requireView().context!!)
            builder.setTitle(getString(R.string.about_app))
            builder.setContentView(view)
            builder.show()

        }

        private fun support() {

            val view: View = layoutInflater.inflate(R.layout.help_and_support, null)
            val builder = BottomSheetDialog(requireView().context!!)
            builder.setTitle(getString(R.string.help_and_support))
            val tvPhoneNumber = view.callNumber_ID
            val tvEmailAddress = view.sendEmail

            tvPhoneNumber.setOnClickListener(View.OnClickListener() {
                val phone = "+966569861476"
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phone")
                startActivity(intent)

            });

            tvEmailAddress.setOnClickListener {
                val email = "Basil_alluqmni@hotmail.com"
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:") // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, "${email}")
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
                if (activity?.let { it -> intent.resolveActivity(it.packageManager) } != null) {
                    startActivity(intent)
                }
            }
            builder.setContentView(view)
            builder.show()

        }
    }

