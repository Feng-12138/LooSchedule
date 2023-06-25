package entities

import javax.persistence.*

@Entity
@Table(name = "Minor")
class Minor {
    @Id
    @Column(name = "requirementID", nullable = false)
    var requirementID: Long = 0

    @Column(name = "minorName", nullable = false)
    var minorName: String = ""
}