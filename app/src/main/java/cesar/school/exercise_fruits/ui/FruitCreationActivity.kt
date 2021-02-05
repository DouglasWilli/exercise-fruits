package cesar.school.exercise_fruits.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import cesar.school.exercise_fruits.R

import cesar.school.exercise_fruits.databinding.ActivityFruitCreationBinding
import cesar.school.exercise_fruits.model.Fruit
import cesar.school.exercise_fruits.data.MockData.initialFruits

class FruitCreationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFruitCreationBinding

    private var listNewPhotos = MainActivity.listNewPhotos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFruitCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarfruitAdd)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.activity_add_fruit)

        if (savedInstanceState != null) {
            val bitmap: Bitmap? = savedInstanceState.getParcelable<Bitmap>(SAVED_FRUIT_PHOTO)
            bitmap?.let {
                newFruitPhoto = it;
                binding.imageFruitPreview.setImageBitmap(bitmap)
            }
        }

        addFruit()
        uploadImage()
    }

    private fun uploadImage() {
        binding.fabPhoto.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, GALLERY_PICTURE)
            } catch (e: ActivityNotFoundException) {
                Log.e("TAG", "No gallery: $e")
            }
        }
    }

    private fun addFruit() {
        binding.buttonCreate.setOnClickListener {
            val name = binding.inputFruitName.text.toString()
            val benefits = binding.inputFruitBeneficit.text.toString()
            val lastFruitId = initialFruits.last().id

            if (name.isNotEmpty() && benefits.isNotEmpty() && newFruitPhoto != null) {
                newFruitPhoto?.let { listNewPhotos.add(it) }
                newFruitPhoto = null

                val newFruit = Fruit(lastFruitId + 1, name, benefits, photoAdded = listNewPhotos.lastIndex)
                val returnIntent = Intent()
                returnIntent.putExtra(MainActivity.MAIN_ACTIVITY_FRUIT_ADDED_ID, newFruit)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            } else if (newFruitPhoto == null) {
                Toast.makeText(this, R.string.select_image, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.fields_image, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_PICTURE) {
            val source = data?.data?.let { ImageDecoder.createSource(this.contentResolver, it) }
            val bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
            bitmap?.let { newFruitPhoto = it }
            binding.imageFruitPreview.setImageBitmap(bitmap)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(SAVED_FRUIT_PHOTO, newFruitPhoto)
    }

    companion object {
        const val GALLERY_PICTURE = 1
        const val SAVED_FRUIT_PHOTO = "save_fruit_photo"
        var newFruitPhoto: Bitmap? = null
    }
}