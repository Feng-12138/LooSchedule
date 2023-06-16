package entities

import javax.persistence.*

typealias ListNumber = Int

@Entity
@Table(name = "Communication")
class Communication {
    @Id
    @Column(name = "courseID", nullable = false)
    var courseID: CourseId = ""

    @Column(name = "subject", nullable = false)
    var subject: Subject = ""

    @Column(name = "code", nullable = false)
    var code: Code = ""

    @Column(name = "listNumber", nullable = false)
    var listNumber: ListNumber = 0

    @Column(name = "year", nullable = false)
    var year: Year = ""
}
