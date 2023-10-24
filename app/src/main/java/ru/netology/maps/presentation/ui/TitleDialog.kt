package ru.netology.maps.presentation.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import ru.netology.maps.R
import ru.netology.maps.presentation.viewModel.LocationViewModel


class TitleDialog(val latitude: Double, val longitude: Double) : DialogFragment() {
    private val viewModel: LocationViewModel by activityViewModels()

    fun newInstance(titleDialog: TitleDialog): DialogFragment {
        return TitleDialog(latitude = 0.0, longitude = 0.0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showsDialog = true;
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: View = inflater.inflate(R.layout.title_dialog, container, false)
        val save = v.findViewById<Button>(R.id.save)
        val text1 = v.findViewById<EditText>(R.id.title)

        save?.setOnClickListener {
            if (longitude != 0.0 || latitude != 0.0) {
                viewModel.saveLocation(latitude, longitude, text1?.text.toString())
            } else {
                viewModel.changeTitle(text1?.text.toString())

            }
            dialog?.dismiss()
        }

        return v
    }

}
