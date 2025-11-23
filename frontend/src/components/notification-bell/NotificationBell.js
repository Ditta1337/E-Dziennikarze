import React, {useState, useEffect} from 'react';
import {
    IconButton,
    Badge,
    Popover,
    List,
    ListItem,
    ListItemText,
    Typography,
    Divider,
    CircularProgress,
    Box,
    Button,
    Snackbar,
    Alert
} from '@mui/material';
import './NotificationBell.scss';
import NotificationsIcon from '@mui/icons-material/Notifications';
import {get, patch} from '../../api';
import {useStore} from "../../store";
import useWebSocket from "../../websocket/useWebSocket";

const NotificationBell = () => {
    const userId = useStore((state) => state.user.userId);
    const [notifications, setNotifications] = useState([]);
    const [anchorEl, setAnchorEl] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");

    const { on, off } = useWebSocket('/ws/notification');

    useEffect(() => {
        fetchUnreadNotifications();

        const handleNewNotification = (message) => {
            setNotifications(prev => [message, ...prev]);
        };

        const handleConnect = () => console.log("Notification WebSocket connected.");
        const handleDisconnect = () => console.log("Notification WebSocket disconnected.");
        const handleError = (error) => {
            console.error("Notification WebSocket error:", error);
            setSnackbarMessage("Błąd połączenia z serwerem powiadomień");
            setSnackbarSeverity("error");
            setSnackbarOpen(true);
        }

        on('message', handleNewNotification);
        on('open', handleConnect);
        on('close', handleDisconnect);
        on('error', handleError);

        return () => {
            off('message', handleNewNotification);
            off('open', handleConnect);
            off('close', handleDisconnect);
            off('error', handleError);
        };
    }, [on, off]);

    const fetchUnreadNotifications = async () => {
        try {
            setIsLoading(true);
            const response = await get(`/notification/unread/user/${userId}`);
            setNotifications(response.data);
        } catch (error) {
            console.error("Error fetching notifications:", error);
            setSnackbarMessage("Nie udało się pobrać powiadomień");
            setSnackbarSeverity("error");
            setSnackbarOpen(true);
        } finally {
            setIsLoading(false);
        }
    };

    const handleMarkAsRead = async (notificationId) => {
        try {
            await patch(`/notification/${notificationId}/read`);
            setNotifications(prev => prev.filter(n => n.id !== notificationId));
        } catch (error) {
            console.error("Error marking notification as read:", error);
            setSnackbarMessage("Wystąpił błąd");
            setSnackbarSeverity("error");
            setSnackbarOpen(true);
        }
    };

    const handleMarkAllAsRead = async () => {
        try {
            await patch('/notification/read-all');
            setNotifications([]);
            handleClose();
        } catch (error) {
            console.error("Error marking all notifications as read:", error);
            setSnackbarMessage("Wystąpił błąd");
            setSnackbarSeverity("error");
            setSnackbarOpen(true);
        }
    };

    const handleBellClick = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const handleSnackbarClose = (event, reason) => {
        if (reason === "clickaway") {
            return;
        }
        setSnackbarOpen(false);
    };

    const open = Boolean(anchorEl);

    const formatDateTime = (dateString) => {
        if (!dateString) return '';
        const isoString = dateString.replace(' ', 'T').replace(' +00:00', 'Z');
        const date = new Date(isoString);
        return date.toLocaleDateString('pl-PL', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            hour12: false,
            timeZone: 'Europe/Warsaw'
        });
    };

    return (
        <Box className="notification-bell">
            <IconButton color="inherit" onClick={handleBellClick}>
                <Badge badgeContent={notifications.length} color="error">
                    <NotificationsIcon/>
                </Badge>
            </IconButton>
            <Popover
                open={open}
                anchorEl={anchorEl}
                onClose={handleClose}
                anchorOrigin={{
                    vertical: 'bottom',
                    horizontal: 'right',
                }}
                transformOrigin={{
                    vertical: 'top',
                    horizontal: 'right',
                }}
                className="notification-popover"
            >
                <Box className="list-header">
                    <Typography variant="h6">Powiadomienia</Typography>
                </Box>
                <Divider/>
                <List className="notification-list">
                    {isLoading ? (
                        <Box className="center-content">
                            <CircularProgress/>
                        </Box>
                    ) : notifications.length > 0 ? (
                        notifications.map((notification) => (
                            <ListItem
                                key={notification.id}
                                button
                                onClick={() => handleMarkAsRead(notification.id)}
                                className="notification-item"
                            >
                                <ListItemText primary={notification.message} secondary={formatDateTime(notification.created_at)}/>
                            </ListItem>
                        ))
                    ) : (
                        <Box className="center-content empty-message">
                            <Typography>Brak nowych powiadomień</Typography>
                        </Box>
                    )}
                    {notifications.length > 0 && (
                        <Box className="mark-all-read">
                            <Button size="small" onClick={handleMarkAllAsRead}>Oznacz wszystkie jako
                                przeczytane</Button>
                        </Box>
                    )}
                </List>
            </Popover>
            <Snackbar
                open={snackbarOpen}
                autoHideDuration={6000}
                onClose={handleSnackbarClose}
                anchorOrigin={{vertical: "bottom", horizontal: "left"}}
            >
                <Alert onClose={handleSnackbarClose} severity={snackbarSeverity} sx={{width: '100%'}}>
                    {snackbarMessage}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default NotificationBell;