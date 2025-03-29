import { toast } from 'vue3-toastify';
import "vue3-toastify/dist/index.css";

export enum ToastType {
    Success = "success",
    Info = "info",
    Warn = "warning",
    Error = "error"
}

export default function showToast(message : String, type : ToastType = ToastType.Info ) {
    toast( message, {
            "theme": "auto",
            "type": type,
            "position": "top-center",
            "pauseOnFocusLoss": false,
            "autoClose": 2000,
            "hideProgressBar": true,
            "transition": "slide",
            "dangerouslyHTMLString": true
          }
    )
}