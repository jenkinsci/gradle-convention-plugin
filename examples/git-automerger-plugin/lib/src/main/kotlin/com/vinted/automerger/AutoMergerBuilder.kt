package com.vinted.automerger

import com.vinted.automerger.config.MergeRule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.Serializable

class AutoMergerBuilder : Serializable {

    var releaseBranchPattern = "release/%"
        private set

    val mergeConfigs = mutableListOf<MergeRule>()

    var pathToRepo: File? = null
        private set

    @Transient
    var logger: Logger = LoggerFactory.getLogger(AutoMerger::class.java)
        private set

    var remoteName: String = "origin"
        private set

    var checkoutFromRemote: Boolean = false
        private set

    var detailConflictReport: Boolean = false
        private set

    var limitAuthorsInDetailReport: Int = 3
        private set

    var limitCommitsInDetailReport: Int = 3
        private set

    /**
     * Release branch pattern must contain `%` which denotes to version.
     * For example "release/%" is applicable release/8.10 and release/0.1
     */
    fun releaseBranchPattern(releaseBranchPattern: String) = apply { this.releaseBranchPattern = releaseBranchPattern }

    /**
     * Only configured conflicts are solved.
     */
    fun addMergeConfig(mergeRule: MergeRule) = apply { this.mergeConfigs += mergeRule }

    /**
     * Path to repo where `.git` folder exist
     */
    fun pathToRepo(pathToRepo: File) = apply { this.pathToRepo = pathToRepo }

    fun logger(logger: Logger) = apply { this.logger = logger }

    /**
     * If enabled, the library will checkout remote branches before merging
     */
    fun checkoutFromRemote(enabled: Boolean) = apply { this.checkoutFromRemote = enabled }

    /**
     * @param remote name of remote branch.
     * Do not work if [checkoutFromRemote] is disabled.
     * Default `origin`
     */
    fun remoteName(remote: String) = apply { this.remoteName = remote }

    /**
     * Enables details report in case of conflict.
     * Detail report includes summary of conflicting lines by author.
     * It helps determine which author will be easier to solve conflict.
     * Detail report will be included in exception message.
     */
    fun detailConflictReport(enabled: Boolean) = apply { this.detailConflictReport = enabled }

    /**
     * Maximum number of report authors if [detailConflictReport] is enabled.
     * Large number could bloats report very fast.
     * @param number must be at least 1
     */
    fun limitAuthorsInDetailReport(number: Int): AutoMergerBuilder {
        if (number < 1) throw IllegalArgumentException("limit author must be at least 1")

        return apply { this.limitAuthorsInDetailReport = number }
    }

    /**
     * Maximum number of reported commits for each author if [detailConflictReport] is enabled.
     * Large number could bloats report very fast.
     * @param number must 0 or positive number. 0 disables commit reports at all
     */
    fun limitCommitsInDetailReport(number: Int) = apply { this.limitCommitsInDetailReport = number }

    fun build() = AutoMerger(this)
}
