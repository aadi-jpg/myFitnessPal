package com.example.myfitnesspal.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun saveUser(user: User, onResult: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: run {
            onResult(false, "No logged in user")
            return
        }
        db.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun getUser(uid: String, onResult: (User?) -> Unit) {
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { snap ->
                val user = snap.toObject(User::class.java)
                onResult(user)
            }
            .addOnFailureListener { _ -> onResult(null) }
    }

    fun updateUser(user: User, onResult: (Boolean) -> Unit) {
        db.collection("users").document(user.uid)
            .set(user)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun addWorkout(uid: String, workout: Workout, onResult: (Boolean) -> Unit) {
        val docRef = db.collection("users").document(uid)
            .collection("workouts").document()
        val workoutWithId = workout.copy(id = docRef.id)

        val data = mapOf(
            "id" to workoutWithId.id,
            "type" to workoutWithId.type,
            "duration" to workoutWithId.duration,
            "calories" to workoutWithId.calories.toDouble(),
            "caloriesBurned" to workoutWithId.calories.toDouble(),
            "date" to workoutWithId.date
        )

        docRef.set(data)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun addMeal(uid: String, meal: Meal, onResult: (Boolean) -> Unit) {
        val docRef = db.collection("users").document(uid)
            .collection("nutrition").document()
        val mealWithId = meal.copy(id = docRef.id)

        val data = mapOf(
            "id" to mealWithId.id,
            "food" to mealWithId.food,
            "calories" to mealWithId.calories.toDouble(),
            "protein" to mealWithId.protein,
            "carbs" to mealWithId.carbs,
            "fats" to mealWithId.fats,
            "date" to mealWithId.date
        )

        docRef.set(data)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getWorkouts(uid: String, onResult: (List<Workout>) -> Unit) {
        db.collection("users").document(uid)
            .collection("workouts")
            .get()
            .addOnSuccessListener { snapshot ->
                val workouts = snapshot.documents.mapNotNull { it.toObject(Workout::class.java) }
                onResult(workouts)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getMealsForDate(uid: String, date: String, onResult: (List<Meal>) -> Unit) {
        db.collection("users").document(uid)
            .collection("nutrition")
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { snapshot ->
                val meals = snapshot.documents.mapNotNull { it.toObject(Meal::class.java) }
                onResult(meals)
            }
            .addOnFailureListener { _ -> onResult(emptyList()) }
    }

    fun addProgress(uid: String, entry: ProgressEntry, onResult: (Boolean) -> Unit) {
        val docRef = db.collection("users").document(uid)
            .collection("progress").document()
        val entryWithId = entry.copy(id = docRef.id)
        docRef.set(entryWithId)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getProgress(uid: String, onResult: (List<ProgressEntry>) -> Unit) {
        db.collection("users").document(uid)
            .collection("progress")
            .get()
            .addOnSuccessListener { snapshot ->
                val entries = snapshot.documents.mapNotNull { it.toObject(ProgressEntry::class.java) }
                onResult(entries)
            }
            .addOnFailureListener { _ -> onResult(emptyList()) }
    }

    fun addPlan(uid: String, plan: PlanItem, onResult: (Boolean) -> Unit) {
        val docRef = db.collection("users").document(uid)
            .collection("plans").document()
        val planWithId = plan.copy(id = docRef.id)
        docRef.set(planWithId)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getPlans(uid: String, onResult: (List<PlanItem>) -> Unit) {
        db.collection("users").document(uid)
            .collection("plans")
            .get()
            .addOnSuccessListener { snapshot ->
                val items = snapshot.documents.mapNotNull { it.toObject(PlanItem::class.java) }
                onResult(items)
            }
            .addOnFailureListener { _ -> onResult(emptyList()) }
    }

    // --- TODAY-ONLY versions ---

    fun getWorkoutsForDate(uid: String, date: String, onResult: (List<Workout>) -> Unit) {
        db.collection("users").document(uid)
            .collection("workouts")
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { snapshot ->
                val workouts = snapshot.documents.mapNotNull { it.toObject(Workout::class.java) }
                onResult(workouts)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun listenToDashboardForDate(
        uid: String,
        date: String,
        onUpdate: (Int /*consumed*/, Int /*burned*/, Int /*workouts*/) -> Unit
    ): ListenerRegistration {
        val nutritionQuery = db.collection("users").document(uid).collection("nutrition")
            .whereEqualTo("date", date)
        val workoutsQuery = db.collection("users").document(uid).collection("workouts")
            .whereEqualTo("date", date)

        val nutritionListener = nutritionQuery.addSnapshotListener { nutritionSnapshot, _ ->
            val consumed = nutritionSnapshot?.documents
                ?.sumOf { it.getDouble("calories")?.toInt() ?: 0 } ?: 0

            workoutsQuery.get().addOnSuccessListener { workoutSnapshot ->
                val burned = workoutSnapshot.documents
                    .sumOf { it.getDouble("caloriesBurned")?.toInt() ?: 0 }
                val workoutsCount = workoutSnapshot.size()
                onUpdate(consumed, burned, workoutsCount)
            }.addOnFailureListener {
                onUpdate(consumed, 0, 0)
            }
        }

        return object : ListenerRegistration {
            override fun remove() {
                nutritionListener.remove()
            }
        }
    }
}
