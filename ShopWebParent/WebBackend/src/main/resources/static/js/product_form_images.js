var extraImageCount = 0;

$(document).ready(function() {
	$("input[name='extraImage']").each(function(index) {
		extraImageCount++;
		$(this).change(function() {
			if (!checkFileSize(this)) {
				return;
			}
			showExtraThumbnail(this, index);
		});
	});

	$("a[name='linkRemoveExtraImage']").each(function(index) {
		$(this).click(function() {
			removeExtraImage(index);
		}); 
	});

});

function showExtraThumbnail(fileInput, index) {
	var file = fileInput.files[0];
	
	fileName = file.name;
	imageNameHiddenField = $("#imageName" + index);
	if(imageNameHiddenField.length){
		imageNameHiddenField.val(fileName);
	}
	
	
	var reader = new FileReader();
	reader.onload = function(e) {
		$("#extraThumbnail" + index).attr("src", e.target.result);
	};
	reader.readAsDataURL(file);

	if (index >= extraImageCount - 1) {
		addNextExtraImageSection(index + 1);
	}
};

function addNextExtraImageSection(index) {
	htmlExtraImage = `
			<div class="col border m-3 p-2" id="divExtraImage${index}">
				<div id="extraImageHeader${index}"><label>Extra image ${index + 1}:</label></div>
				<div>
					<img src="${defaultImageThumbnailSrc}" style="max-width: 400px;"
					 alt="Extra image" id="extraThumbnail${index}" class="img-fluid"/>
				</div>
				<div>
					<input type="file" name="extraImage" accept="image/png,image/jpeg, image/jpg" 
					onchange="showExtraThumbnail(this, ${index})" />
				</div>
			</div>
	`;

	htmlRemove = `
		<a class="btn fas fa-times-circle float-right" title="Remove this image"
		href="javascript:removeExtraImage(${index - 1})" ></a>
	`;

	$("#divProductImages").append(htmlExtraImage);
	$("#extraImageHeader" + (index - 1)).append(htmlRemove);
	extraImageCount++;

}

function removeExtraImage(index) {
	$("#divExtraImage" + index).remove();
}

