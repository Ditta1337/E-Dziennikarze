import React, { useEffect, useState, useRef, useLayoutEffect } from "react";
import { useFormik, FormikProvider, Form } from "formik";
import * as Yup from "yup";
import {
    Autocomplete,
    Box,
    TextField,
    Paper,
    List,
    ListItem,
    ListItemText,
    ListItemAvatar,
    Avatar,
    Typography,
    Button,
    Snackbar,
    Alert,
    CircularProgress
} from "@mui/material";
import PersonIcon from '@mui/icons-material/Person';

import { useStore } from "../../store";
import { get } from "../../api";
import useWebSocket from "../../websocket/useWebSocket";

import MessageBubble from "../../components/chat/message-bubble/MessageBubble";
import Modal from "../../components/modal/Modal";

import "./Chat.scss";

const PAGE_SIZE = 20;

const MessageSchema = Yup.object({
    content: Yup.string().required("Wiadomość nie może być pusta"),
});

const normalizeMessage = (data) => ({
    ...data,
    id: data.id,
    content: data.content,
    status: data.status,
    type: data.type,
    senderId: data.senderId || data.sender_id,
    receiverId: data.receiverId || data.receiver_id,
    created_at: data.created_at || data.createdAt
});

function Chat() {
    const { user, chatHistory, addToChatHistory } = useStore();
    const myId = user?.userId;

    const { isConnected, send, on, off } = useWebSocket('/ws/chat');

    const messageListRef = useRef(null);
    const prevScrollHeightRef = useRef(null);

    const [users, setUsers] = useState([]);
    const [usersSelectData, setUsersSelectData] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);

    const [messages, setMessages] = useState([]);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const [isFetchingHistory, setIsFetchingHistory] = useState(false);

    const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "info" });
    const [editModalOpen, setEditModalOpen] = useState(false);
    const [deleteModalOpen, setDeleteModalOpen] = useState(false);
    const [messageToAction, setMessageToAction] = useState(null);
    const [editContent, setEditContent] = useState("");

    useEffect(() => {
        fetchUsers();
    }, []);

    useEffect(() => {
        if (myId && selectedUser) {
            setMessages([]);
            setPage(0);
            setHasMore(true);
            fetchMessages(0, true);
            addToChatHistory(selectedUser);
        } else {
            setMessages([]);
        }
    }, [myId, selectedUser]);

    useEffect(() => {
        if (messageListRef.current && !isFetchingHistory && page === 0) {
            messageListRef.current.scrollTop = messageListRef.current.scrollHeight;
        }
    }, [messages, isFetchingHistory, page]);

    useLayoutEffect(() => {
        if (messageListRef.current && prevScrollHeightRef.current) {
            const newScrollHeight = messageListRef.current.scrollHeight;
            const diff = newScrollHeight - prevScrollHeightRef.current;
            messageListRef.current.scrollTop = diff;
            prevScrollHeightRef.current = null;
        }
    }, [messages]);

    useEffect(() => {
        const handleMessage = (incomingData) => {
            if (!selectedUser || !myId) return;

            const normalizedMsg = normalizeMessage(incomingData);
            const isRelevant =
                (normalizedMsg.senderId === myId && normalizedMsg.receiverId === selectedUser.id) ||
                (normalizedMsg.senderId === selectedUser.id && normalizedMsg.receiverId === myId);

            if (!isRelevant) return;

            setMessages(prev => {
                if (normalizedMsg.status === 'DELETED') {
                    return prev.map(m => m.id === normalizedMsg.id ? { ...m, status: 'DELETED', content: normalizedMsg.content } : m);
                }
                const exists = prev.some(m => m.id === normalizedMsg.id);
                if (exists) {
                    return prev.map(m => m.id === normalizedMsg.id ? normalizedMsg : m);
                }
                return [...prev, normalizedMsg];
            });
        };

        const handleOpen = () => setSnackbar({ open: true, message: "Połączono!", severity: "success" });
        const handleError = () => console.error("WS Error");

        on('open', handleOpen);
        on('message', handleMessage);
        on('error', handleError);

        return () => {
            off('open', handleOpen);
            off('message', handleMessage);
            off('error', handleError);
        };
    }, [on, off, selectedUser, myId]);

    const fetchUsers = async () => {
        try {
            const response = await get('/user/all');
            const mappedUsers = response.data.map(u => ({
                ...u,
                label: `${u.name} ${u.surname} (${u.email})`
            }));
            setUsers(mappedUsers);
            setUsersSelectData(mappedUsers.map(u => ({
                value: u.id,
                label: u.label
            })));
        } catch (error) {
            console.error(error);
            setSnackbar({ open: true, message: "Błąd pobierania użytkowników", severity: "error" });
        }
    };

    const fetchMessages = async (pageNum, isInitial = false) => {
        try {
            if (!isInitial) setIsFetchingHistory(true);
            const response = await get(`message/history/${myId}/${selectedUser.id}?page=${pageNum}&size=${PAGE_SIZE}`);
            const newMessages = response.data;

            if (newMessages.length < PAGE_SIZE) setHasMore(false);

            const sortedNewMessages = newMessages.sort((a, b) =>
                new Date(a.created_at || a.createdAt) - new Date(b.created_at || b.createdAt)
            ).map(normalizeMessage);

            setMessages(prev => {
                if (isInitial) return sortedNewMessages;
                return [...sortedNewMessages, ...prev];
            });
            if (!isInitial) setIsFetchingHistory(false);
        } catch (error) {
            console.error(error);
            setIsFetchingHistory(false);
            setSnackbar({ open: true, message: "Błąd pobierania historii", severity: "error" });
        }
    };

    const handleScroll = (e) => {
        const { scrollTop, scrollHeight } = e.currentTarget;
        if (scrollTop === 0 && hasMore && !isFetchingHistory && messages.length > 0) {
            prevScrollHeightRef.current = scrollHeight;
            const nextPage = page + 1;
            setPage(nextPage);
            fetchMessages(nextPage, false);
        }
    };

    const formik = useFormik({
        initialValues: { content: "" },
        validationSchema: MessageSchema,
        onSubmit: (values, { setSubmitting, resetForm }) => {
            if (!isConnected || !selectedUser) return setSubmitting(false);
            send({
                action: "NEW",
                message: {
                    receiver_id: selectedUser.id,
                    content: values.content,
                    type: "TEXT"
                }
            });
            resetForm();
            setSubmitting(false);
        },
    });

    const openEditModal = (message) => {
        setMessageToAction(message);
        setEditContent(message.content);
        setEditModalOpen(true);
    };

    const confirmEdit = () => {
        if (!messageToAction || !editContent.trim()) return;
        const payload = {
            action: "EDIT",
            message: {
                id: messageToAction.id,
                content: editContent,
                sender_id: messageToAction.senderId,
                receiver_id: messageToAction.receiverId,
                type: messageToAction.type,
                created_at: messageToAction.created_at
            }
        };

        send(payload);
        setEditModalOpen(false);
        setMessageToAction(null);
    };

    const openDeleteModal = (message) => {
        setMessageToAction(message);
        setDeleteModalOpen(true);
    };

    const confirmDelete = () => {
        if (!messageToAction) return;
        send({ action: "DELETE", message: { id: messageToAction.id } });
        setDeleteModalOpen(false);
        setMessageToAction(null);
    };

    const handleUserChange = (_, option) => {
        const user = option ? users.find(u => u.id === option.value) : null;
        setSelectedUser(user);
    };

    const handleHistoryClick = (historyUser) => {
        setSelectedUser(historyUser);
    };

    const handleSnackbarClose = () => setSnackbar({ ...snackbar, open: false });

    if (!myId) {
        return <Box className="chat-view-container" display="flex" justifyContent="center" alignItems="center"><CircularProgress /></Box>;
    }

    return (
        <Box className="chat-view-container">
            <Box className="chat-sidebar">
                <Box className="sidebar-header">
                    <Typography variant="subtitle1">Ostatnie rozmowy</Typography>
                </Box>
                <List className="history-list">
                    {chatHistory.length === 0 && (
                        <Typography variant="body2" color="textSecondary" align="center" sx={{ mt: 2 }}>
                            Brak historii
                        </Typography>
                    )}
                    {chatHistory.map((u) => (
                        <ListItem
                            key={u.id}
                            className={`history-item ${selectedUser?.id === u.id ? 'active' : ''}`}
                            onClick={() => handleHistoryClick(u)}
                        >
                            <ListItemAvatar>
                                <Avatar><PersonIcon /></Avatar>
                            </ListItemAvatar>
                            <ListItemText
                                primary={`${u.name} ${u.surname}`}
                                secondary={u.email}
                                primaryTypographyProps={{ noWrap: true }}
                                secondaryTypographyProps={{ noWrap: true, style: { fontSize: '0.8rem' } }}
                            />
                        </ListItem>
                    ))}
                </List>
            </Box>

            <Box className="chat-main-area">
                <Box className="chat-header">
                    <Autocomplete
                        options={usersSelectData}
                        getOptionLabel={(option) => option.label}
                        isOptionEqualToValue={(option, value) => option.value === value.value}
                        onChange={handleUserChange}
                        value={selectedUser ? { value: selectedUser.id, label: selectedUser.label || `${selectedUser.name} ${selectedUser.surname} (${selectedUser.email})` } : null}
                        renderInput={(params) => (
                            <TextField {...params} label="Rozpocznij nową rozmowę..." variant="outlined" size="small" />
                        )}
                        className="user-select"
                    />
                    <Box className={`connection-status ${isConnected ? 'online' : 'offline'}`} />
                </Box>

                <Paper className="message-container" ref={messageListRef} elevation={0} onScroll={handleScroll}>
                    {selectedUser ? (
                        <>
                            {isFetchingHistory && (
                                <Box display="flex" justifyContent="center" p={1}>
                                    <CircularProgress size={20} />
                                </Box>
                            )}
                            <List>
                                {messages.map((msg) => (
                                    <MessageBubble
                                        key={msg.id}
                                        message={msg}
                                        currentUserId={myId}
                                        onEdit={openEditModal}
                                        onDelete={openDeleteModal}
                                    />
                                ))}
                            </List>
                        </>
                    ) : (
                        <Box className="empty-state">
                            <Box>
                                <Typography variant="h6" color="textSecondary" gutterBottom>
                                    Wybierz rozmowę z listy
                                </Typography>
                                <Typography variant="body2" color="textSecondary">
                                    lub wyszukaj użytkownika powyżej.
                                </Typography>
                            </Box>
                        </Box>
                    )}
                </Paper>

                <Box className="input-area">
                    <FormikProvider value={formik}>
                        <Form autoComplete="off">
                            <Box display="flex" gap={2}>
                                <TextField
                                    fullWidth
                                    name="content"
                                    placeholder={selectedUser ? "Napisz wiadomość..." : "Wybierz rozmówcę"}
                                    value={formik.values.content}
                                    onChange={formik.handleChange}
                                    disabled={!selectedUser}
                                    variant="outlined"
                                    size="small"
                                />
                                <Button
                                    variant="contained"
                                    type="submit"
                                    disabled={formik.isSubmitting || !isConnected || !selectedUser || !formik.values.content}
                                >
                                    Wyślij
                                </Button>
                            </Box>
                        </Form>
                    </FormikProvider>
                </Box>
            </Box>

            <Snackbar open={snackbar.open} autoHideDuration={4000} onClose={handleSnackbarClose}>
                <Alert onClose={handleSnackbarClose} severity={snackbar.severity} sx={{ width: '100%' }}>
                    {snackbar.message}
                </Alert>
            </Snackbar>

            <Modal isOpen={editModalOpen} onClose={() => setEditModalOpen(false)} className="chat-modal">
                <Typography variant="h6" gutterBottom>Edytuj wiadomość</Typography>
                <TextField
                    fullWidth multiline rows={3}
                    value={editContent}
                    onChange={(e) => setEditContent(e.target.value)}
                    variant="outlined" sx={{ my: 2 }}
                />
                <Box display="flex" justifyContent="flex-end" gap={1}>
                    <Button onClick={() => setEditModalOpen(false)}>Anuluj</Button>
                    <Button variant="contained" onClick={confirmEdit}>Zapisz</Button>
                </Box>
            </Modal>

            <Modal isOpen={deleteModalOpen} onClose={() => setDeleteModalOpen(false)} className="chat-modal">
                <Typography variant="h6" gutterBottom>Usuń wiadomość</Typography>
                <Typography sx={{ my: 2 }}>Czy na pewno chcesz usunąć tę wiadomość?</Typography>
                <Box display="flex" justifyContent="flex-end" gap={1}>
                    <Button onClick={() => setDeleteModalOpen(false)}>Anuluj</Button>
                    <Button variant="contained" color="error" onClick={confirmDelete}>Usuń</Button>
                </Box>
            </Modal>
        </Box>
    );
}

export default Chat;