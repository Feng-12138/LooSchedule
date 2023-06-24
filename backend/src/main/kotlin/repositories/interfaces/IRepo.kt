package repositories.interfaces

import entities.Communication
import entities.Course

interface IRepo<T> {
    fun getAll(): List<T>
    fun getById(): T
}