package entities

import javax.persistence.*

typealias CourseId = String
typealias CourseName = String
typealias CourseSubject = String
typealias CourseCode = String
typealias Description = String
typealias Credit = Double
typealias Availability = String
typealias OnlineTerms = String
typealias Coreqs = String
typealias Antireqs = String
typealias LikedRating = Double
typealias EasyRating = Double
typealias UsefulRating = Double

@Entity
@Table(name = "Course")
class Course {

    enum class CourseRelatedType {
        BREADTH,
        PREREQ
    }

    @Id
    @Column(name = "courseID", nullable = false)
    var courseID: CourseId = ""

    @Column(name = "courseName", nullable = false)
    var courseName: CourseName = ""

    @Column(name = "subject", nullable = false)
    var subject: CourseSubject = ""

    @Column(name = "code", nullable = false)
    var code: CourseCode = ""

    @Column(name = "description", nullable = false)
    var description: Description = ""

    @Column(name = "credit", nullable = false)
    var credit: Credit = 0.0

    @Column(name = "availability")
    var availability: Availability? = null

    @Column(name = "onlineTerms")
    var onlineTerms: OnlineTerms? = null

    @Column(name = "coreqs")
    var coreqs: Coreqs? = null

    @Column(name = "antireqs")
    var antireqs: Antireqs? = null

    @Column(name = "likedRating")
    var likedRating: LikedRating? = 0.0

    @Column(name = "easyRating")
    var easyRating: EasyRating? = 0.0

    @Column(name = "usefulRating")
    var usefulRating: UsefulRating? = 0.0

//    @OneToOne(cascade = [CascadeType.ALL])
//    @PrimaryKeyJoinColumn
//    lateinit var breadth: Breadth

    @OneToOne(cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    lateinit var prereqs: Prerequisite
}



