package com.example.myapplication.home.fragment.studentFragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.myapplication.databinding.StudentCardLayoutBinding
import com.example.myapplication.home.fragment.studentFragment.StudentFragment
import com.example.myapplication.model.Student

class StudentAdapter(
    private val listener: StudentFragment
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    private var students: MutableList<Student> = mutableListOf()

    inner class StudentViewHolder(
        private val binding: StudentCardLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(student: Student) {
            binding.ivStudentProfile.load(student.details?.imageUrl)
            binding.tvStudentName.text = student.details?.username
            binding.tvStudentEmail.text = student.details?.email

            binding.cvStudent.setOnClickListener {
                listener.navigateToStudentView(student)
            }

            binding.ivDeleteStudent.setOnClickListener {
                listener.deleteStudent(student)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = StudentCardLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(student = students[position])
    }

    override fun getItemCount(): Int = students.size

    fun setData(newStudents: List<Student>) {
        students.clear()
        students.addAll(newStudents)
        notifyDataSetChanged()
    }

}