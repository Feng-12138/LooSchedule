package entities

import javax.persistence.*

@Entity
@Table(name = "Major")
class Major {
    @Id
    @Column(name = "requirementID", nullable = false)
    var requirementID: Long = 0

    @Column(name = "majorName", nullable = false)
    var majorName: String = ""

    @Column(name = "isCoop", nullable = false)
    var isCoop: Boolean = true

    @Column(name = "isDoubleDegree", nullable = false)
    var isDoubleDegree: Boolean = false
}