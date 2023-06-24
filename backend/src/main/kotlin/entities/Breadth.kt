package entities

import javax.persistence.*

typealias Category = String
typealias Subject = String
typealias Code = String

@Entity
@Table(name = "Breadth")
class Breadth {
    @Id
    @Column(name = "courseID", nullable = false)
    var courseID: CourseId = ""

    @Column(name = "subject", nullable = false)
    var subject: Subject = ""

    @Column(name = "code", nullable = false)
    var code: Code = ""

    @Column(name = "category", nullable = false)
    var category: Category = ""
}