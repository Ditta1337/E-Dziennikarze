import React, {useState, useCallback} from "react";
import {useDropzone} from "react-dropzone";
import Cropper from "react-easy-crop";
import {Box, Button, Slider, Typography, Paper, IconButton} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import Modal from "../modal/Modal";
import "./ImageAdder.scss";

function ImageAdder({onImageCropped}) {
    const [imageSrc, setImageSrc] = useState(null);
    const [crop, setCrop] = useState({x: 0, y: 0});
    const [zoom, setZoom] = useState(1);
    const [croppedAreaPixels, setCroppedAreaPixels] = useState(null);
    const [showModal, setShowModal] = useState(false);

    const onDrop = useCallback((files) => {
        const file = files[0];
        setImageSrc(URL.createObjectURL(file));
        setShowModal(true);
    }, []);

    const {getRootProps, getInputProps, isDragActive} = useDropzone({
        onDrop,
        accept: {
            'image/jpeg': ['.jpeg', '.jpg'],
            'image/png': ['.png'],
            'image/webp': ['.webp'],
            'image/gif': ['.gif'],
        },
        multiple: false
    });

    const onCropComplete = useCallback((_, areaPixels) => {
        setCroppedAreaPixels(areaPixels);
    }, []);

    const createImage = (url) =>
        new Promise((resolve, reject) => {
            const img = new Image();
            img.addEventListener("load", () => resolve(img));
            img.addEventListener("error", reject);
            img.setAttribute("crossOrigin", "anonymous");
            img.src = url;
        });

    async function getCroppedImg(imageSrc, croppedAreaPixels) {
        const image = await createImage(imageSrc);
        const canvas = document.createElement("canvas");
        const ctx = canvas.getContext("2d");

        canvas.width = croppedAreaPixels.width;
        canvas.height = croppedAreaPixels.height;

        ctx.drawImage(
            image,
            croppedAreaPixels.x,
            croppedAreaPixels.y,
            croppedAreaPixels.width,
            croppedAreaPixels.height,
            0,
            0,
            croppedAreaPixels.width,
            croppedAreaPixels.height
        );

        return new Promise((resolve) => {
            const base64Image = canvas.toDataURL("image/jpeg");
            resolve(base64Image);
        });
    }


    const handleCrop = async () => {
        try {
            const croppedImage = await getCroppedImg(imageSrc, croppedAreaPixels);
            setImageSrc(croppedImage);
            setShowModal(false);

            if (onImageCropped) {
                onImageCropped(croppedImage)
            }
        } catch (e) {
            console.error("Błąd przy wycinaniu obrazka:", e);
        }
    };

    return (
        <Box className="image-adder">
            <Paper {...getRootProps()} className={`dropzone ${isDragActive ? "active" : ""}`}>
                <input {...getInputProps()} />
                {imageSrc ? (
                    <Box className="preview-container">
                        <IconButton className="delete-button" onClick={() => setImageSrc(null)} size="small">
                            <DeleteIcon/>
                        </IconButton>
                        <img src={imageSrc} alt="preview" className="preview"/>
                    </Box>
                ) : (
                    <Typography>
                        Kliknij lub przerzuć zdjęcie
                    </Typography>
                )}
            </Paper>

            <Modal className="cropp-modal" isOpen={showModal} onClose={() => setShowModal(false)}>
                <Box className="image-container">
                    <Cropper
                        image={imageSrc}
                        crop={crop}
                        zoom={zoom}
                        aspect={1}
                        onCropChange={setCrop}
                        onZoomChange={setZoom}
                        onCropComplete={onCropComplete}
                    />
                </Box>
                <Box className="controls">
                    <Typography gutterBottom>Zoom</Typography>
                    <Slider
                        value={zoom}
                        min={1}
                        max={3}
                        step={0.1}
                        onChange={(e, v) => setZoom(v)}
                        className="slider"
                    />
                    <Button variant="contained" onClick={handleCrop}>
                        Przytnij
                    </Button>
                </Box>
            </Modal>
        </Box>
    );
}

export default ImageAdder;
