package cesar.school.exercise_fruits.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Fruit (
        val id: Int,
        val name: String,
        val benefits: String,
        val photo: Int? = null,
        val photoAdded: Int? = null
): Parcelable
