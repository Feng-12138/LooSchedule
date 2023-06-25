package entities

import javax.persistence.*

@Entity
@Table(name = "Specialization")
class Specialization {
    @Id
    @Column(name = "requirementID", nullable = false)
    var requirementID: Long = 0

    @Column(name = "specializationName", nullable = false)
    var specializationName: String = ""
}