package com.example.data

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.ProjectEntity
import com.example.firebase.FirebaseManager
import com.example.model.VideoProject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ProjectRepository(private val context: Context) {
    private val dao = AppDatabase.getInstance(context).projectDao()
    private val firebaseManager = FirebaseManager.getInstance(context)

    val projectsFlow: Flow<List<VideoProject>> = dao.getAllProjectsFlow().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun saveProject(project: VideoProject) = withContext(Dispatchers.IO) {
        dao.insertOrUpdate(ProjectEntity.fromDomain(project))
        firebaseManager.syncProjectToFirestore(project)
    }

    suspend fun deleteProject(id: String) = withContext(Dispatchers.IO) {
        dao.deleteById(id)
    }

    suspend fun getProjectById(id: String): VideoProject? = withContext(Dispatchers.IO) {
        dao.getProjectById(id)?.toDomain()
    }
}
