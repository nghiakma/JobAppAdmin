package com.example.myapplication.home.fragment.jobsFragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.myapplication.R
import com.example.myapplication.databinding.JobCardLayoutBinding
import com.example.myapplication.home.fragment.jobsFragment.JobsFragment
import com.example.myapplication.model.Job

class JobAdapter(
    private val listener: JobsFragment
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {
    private var jobs: MutableList<Job> = mutableListOf()

    inner class JobViewHolder(
        private val binding: JobCardLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(job: Job) {
            with(binding) {
                ivCompanyLogo.load(job.imageUrl) {
                    error(R.drawable.ic_apple_logo)
                    placeholder(R.drawable.ic_jobs)
                    build()
                }
                tvJobRole.text = job.role
                tvCompanyName.text = job.name
                ivDeleteJob.setOnClickListener {
                    listener.deleteJobDialog(job = job)
                }
                cvJob.setOnClickListener {
                    listener.navigateToJobViewFragment(job = job)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        return JobViewHolder(
            JobCardLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(jobs[position])
    }

    override fun getItemCount(): Int = jobs.size

    fun setJobListData(newJobs: List<Job>) {
        jobs.clear()
        jobs.addAll(newJobs)
        notifyDataSetChanged()
    }
}