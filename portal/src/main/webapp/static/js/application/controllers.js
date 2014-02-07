angular.module("appControllers", ["appServices", "angularFileUpload"]).
    controller("JobListCtrl", ["JobService", "$scope", function (JobService, $scope) {
        console.log("In JobListCtrl");



        var loadJobs = function () {
            JobService.getAllJobs().then(function (jobs) {
                $scope.jobs = jobs;
                console.log(jobs);
            });
        };

        loadJobs();
        $scope.refresh = function () {
            loadJobs();
        };
        $scope.fetchDetail = function (jobIndex, jobId) {
            JobService.fetchJob(jobId).then(function (job) {
                $scope.jobs[jobIndex].detail = job;
            });
        };
        $scope.selected = "MyJobs";
        $scope.phases=[{name:"MyJobs", id:1},{name:"ProteinX23 Project", id:2},{name:"ProteinScala Project", id:3}];

        $scope.status = "AllJobs";

        $scope.statuses=[{name:"AllJobs", value:"AllJobs"},{name:"Finished", value:"Finished"},{name:"Launched", value:"Launched"}];


        $scope.onGroupSelect = function (phaseID) {
            $scope.selected = phaseID;
        };


        $scope.onStatusSelect = function(status){
            $scope.status = status;
            console.log($scope.status);
        };


        //job list filter
        $scope.jobFilter = function (job) {
            console.log(job.status + " status: " +$scope.status+ "   result: "+(job.status === $scope.status));
            if($scope.status == "AllJobs"){
                return true;
            }
            if(job.status != $scope.status){
                return false;
            }
            return true;
        };
        $scope.showDetails = function(item) {
            $scope.item = item;
        };

    }]).
    controller("JobController", ["$scope", "$routeParams","JobService", function ($scope, $routeParams,JobService) {

        $scope.jobID = $routeParams.jobID;
        console.log("In Job Controller, Job ID: "+$scope.jobID);

        $scope.fetchSingleJob = function (){
            JobService.fetchJob($scope.jobID).then(function (job) {
                $scope.job_details = job;
            });
        };
        console.log($scope.job_details);
        $scope.fetchSingleJob();
        console.log("Job details : " + $scope.job_details);

    }]).
    controller("MainController", ["$scope", "$routeParams", function ($scope, $routeParams) {
        $scope.appName="WRF";
        console.log("In MainController");
    }]).
    controller("NewJobCtrl", ["$scope", "$routeParams", function ($scope, $routeParams) {
        $scope.expName = '';

        $scope.onItemClick = function (phaseID) {
            $scope.selected = phaseID;
            $scope.jobForm = "static/amber/new"+$scope.selected+"Job.html"
        };

        $scope.createJob = function(){

        };
        console.log($scope.selected);
        console.log("In New Job Controller ...");
    }]).
    controller("FileUploadController", [ '$scope', '$upload', function ($scope, $upload) {
        $scope.onFileSelect = function ($files) {
            //$files: an array of files selected, each file has name, size, and type.
            for (var i = 0; i < $files.length; i++) {
                var file = $files[i];
                $scope.upload = $upload.upload({
                    url: 'amberCtrl/uploadPDB/test', //upload.php script, node.js route, or servlet url
                    method: 'POST',
                    transformRequest: angular.identity,
                    headers: {'Content-Type': 'multipart/form-data'}, withCredential: true,
                    data: {file: file},
                    file: file,

                    // file: $files, //upload multiple files, this feature only works in HTML5 FromData browsers
                    /* set file formData name for 'Content-Desposition' header. Default: 'file' */
                    fileFormDataName: 'myFile' //OR for HTML5 multiple upload only a list: ['name1', 'name2', ...]
                    /* customize how data is added to formData. See #40#issuecomment-28612000 for example */
                    //formDataAppender: function(formData, key, val){}
                }).progress(function (evt) {
                        console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
                    }).success(function (data, status, headers, config) {
                        // file is uploaded successfully
                        console.log("File uploaded successfully")
                        console.log(data);

                    });
                //.error(...)
                //.then(success, error, progress);
            }
        };
    }]);