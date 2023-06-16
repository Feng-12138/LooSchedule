package entities

import javax.persistence.*

typealias CourseID = String
typealias Courses = String
typealias MinimumLevel = String
typealias OnlyOpenTo = String
typealias NotOpenTo = String

@Entity
@Table(name = "Prerequisite")
class Prerequisite {
    @Id
    @Column(name = "courseID", nullable = false)
    var courseID: CourseID = ""

    @Column(name = "consentRequired", nullable = false)
    var consentRequired: Boolean = false

    @Column(name = "courses")
    var courses: Courses? = null

    @Column(name = "minimumLevel")
    var minimumLevel: MinimumLevel? = null

    @Column(name = "onlyOpenTo")
    var onlyOpenTo: OnlyOpenTo? = null

    @Column(name = "notOpenTo")
    var notOpenTo: NotOpenTo? = null
}