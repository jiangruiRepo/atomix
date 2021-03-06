/*
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.protocols.raft.partition.impl;

import com.google.common.base.Preconditions;
import io.atomix.cluster.NodeId;
import io.atomix.cluster.messaging.ClusterCommunicationService;
import io.atomix.cluster.messaging.MessageSubject;
import io.atomix.primitive.session.SessionId;
import io.atomix.protocols.raft.protocol.AppendRequest;
import io.atomix.protocols.raft.protocol.AppendResponse;
import io.atomix.protocols.raft.protocol.CloseSessionRequest;
import io.atomix.protocols.raft.protocol.CloseSessionResponse;
import io.atomix.protocols.raft.protocol.CommandRequest;
import io.atomix.protocols.raft.protocol.CommandResponse;
import io.atomix.protocols.raft.protocol.ConfigureRequest;
import io.atomix.protocols.raft.protocol.ConfigureResponse;
import io.atomix.protocols.raft.protocol.HeartbeatRequest;
import io.atomix.protocols.raft.protocol.HeartbeatResponse;
import io.atomix.protocols.raft.protocol.InstallRequest;
import io.atomix.protocols.raft.protocol.InstallResponse;
import io.atomix.protocols.raft.protocol.JoinRequest;
import io.atomix.protocols.raft.protocol.JoinResponse;
import io.atomix.protocols.raft.protocol.KeepAliveRequest;
import io.atomix.protocols.raft.protocol.KeepAliveResponse;
import io.atomix.protocols.raft.protocol.LeaveRequest;
import io.atomix.protocols.raft.protocol.LeaveResponse;
import io.atomix.protocols.raft.protocol.MetadataRequest;
import io.atomix.protocols.raft.protocol.MetadataResponse;
import io.atomix.protocols.raft.protocol.OpenSessionRequest;
import io.atomix.protocols.raft.protocol.OpenSessionResponse;
import io.atomix.protocols.raft.protocol.PollRequest;
import io.atomix.protocols.raft.protocol.PollResponse;
import io.atomix.protocols.raft.protocol.PublishRequest;
import io.atomix.protocols.raft.protocol.QueryRequest;
import io.atomix.protocols.raft.protocol.QueryResponse;
import io.atomix.protocols.raft.protocol.RaftServerProtocol;
import io.atomix.protocols.raft.protocol.ReconfigureRequest;
import io.atomix.protocols.raft.protocol.ReconfigureResponse;
import io.atomix.protocols.raft.protocol.ResetRequest;
import io.atomix.protocols.raft.protocol.TransferRequest;
import io.atomix.protocols.raft.protocol.TransferResponse;
import io.atomix.protocols.raft.protocol.VoteRequest;
import io.atomix.protocols.raft.protocol.VoteResponse;
import io.atomix.utils.serializer.Serializer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Raft server protocol that uses a {@link ClusterCommunicationService}.
 */
public class RaftServerCommunicator implements RaftServerProtocol {
  private final RaftMessageContext context;
  private final Serializer serializer;
  private final ClusterCommunicationService clusterCommunicator;

  public RaftServerCommunicator(Serializer serializer, ClusterCommunicationService clusterCommunicator) {
    this(null, serializer, clusterCommunicator);
  }

  public RaftServerCommunicator(String prefix, Serializer serializer, ClusterCommunicationService clusterCommunicator) {
    this.context = new RaftMessageContext(prefix);
    this.serializer = Preconditions.checkNotNull(serializer, "serializer cannot be null");
    this.clusterCommunicator = Preconditions.checkNotNull(clusterCommunicator, "clusterCommunicator cannot be null");
  }

  private <T, U> CompletableFuture<U> sendAndReceive(MessageSubject subject, T request, NodeId nodeId) {
    return clusterCommunicator.sendAndReceive(subject, request, serializer::encode, serializer::decode, NodeId.from(nodeId.id()));
  }

  @Override
  public CompletableFuture<OpenSessionResponse> openSession(NodeId nodeId, OpenSessionRequest request) {
    return sendAndReceive(context.openSessionSubject, request, nodeId);
  }

  @Override
  public CompletableFuture<CloseSessionResponse> closeSession(NodeId nodeId, CloseSessionRequest request) {
    return sendAndReceive(context.closeSessionSubject, request, nodeId);
  }

  @Override
  public CompletableFuture<KeepAliveResponse> keepAlive(NodeId nodeId, KeepAliveRequest request) {
    return sendAndReceive(context.keepAliveSubject, request, nodeId);
  }

  @Override
  public CompletableFuture<QueryResponse> query(NodeId nodeId, QueryRequest request) {
    return sendAndReceive(context.querySubject, request, nodeId);
  }

  @Override
  public CompletableFuture<CommandResponse> command(NodeId nodeId, CommandRequest request) {
    return sendAndReceive(context.commandSubject, request, nodeId);
  }

  @Override
  public CompletableFuture<MetadataResponse> metadata(NodeId nodeId, MetadataRequest request) {
    return sendAndReceive(context.metadataSubject, request, nodeId);
  }

  @Override
  public CompletableFuture<JoinResponse> join(NodeId nodeId, JoinRequest request) {
    return sendAndReceive(context.joinSubject, request, nodeId);
  }

  @Override
  public CompletableFuture<LeaveResponse> leave(NodeId nodeId, LeaveRequest request) {
    return sendAndReceive(context.leaveSubject, request, nodeId);
  }

  @Override
  public CompletableFuture<ConfigureResponse> configure(NodeId nodeId, ConfigureRequest request) {
    return sendAndReceive(context.configureSubject, request, nodeId);
  }

  @Override
  public CompletableFuture<ReconfigureResponse> reconfigure(NodeId nodeId, ReconfigureRequest request) {
    return sendAndReceive(context.reconfigureSubject, request, nodeId);
  }

  @Override
  public CompletableFuture<InstallResponse> install(NodeId nodeId, InstallRequest request) {
    return sendAndReceive(context.installSubject, request, nodeId);
  }

  @Override
  public CompletableFuture<TransferResponse> transfer(NodeId nodeId, TransferRequest request) {
    return sendAndReceive(context.transferSubject, request, nodeId);
  }

  @Override
  public CompletableFuture<PollResponse> poll(NodeId nodeId, PollRequest request) {
    return sendAndReceive(context.pollSubject, request, nodeId);
  }

  @Override
  public CompletableFuture<VoteResponse> vote(NodeId nodeId, VoteRequest request) {
    return sendAndReceive(context.voteSubject, request, nodeId);
  }

  @Override
  public CompletableFuture<AppendResponse> append(NodeId nodeId, AppendRequest request) {
    return sendAndReceive(context.appendSubject, request, nodeId);
  }

  @Override
  public void publish(NodeId nodeId, PublishRequest request) {
    clusterCommunicator.unicast(context.publishSubject(request.session()), request, serializer::encode, NodeId.from(nodeId.id()));
  }

  @Override
  public CompletableFuture<HeartbeatResponse> heartbeat(NodeId nodeId, HeartbeatRequest request) {
    return sendAndReceive(context.heartbeatSubject, request, nodeId);
  }

  @Override
  public void registerOpenSessionHandler(Function<OpenSessionRequest, CompletableFuture<OpenSessionResponse>> handler) {
    clusterCommunicator.addSubscriber(context.openSessionSubject, serializer::decode, handler, serializer::encode);
  }

  @Override
  public void unregisterOpenSessionHandler() {
    clusterCommunicator.removeSubscriber(context.openSessionSubject);
  }

  @Override
  public void registerCloseSessionHandler(Function<CloseSessionRequest, CompletableFuture<CloseSessionResponse>> handler) {
    clusterCommunicator.addSubscriber(context.closeSessionSubject, serializer::decode, handler, serializer::encode);
  }

  @Override
  public void unregisterCloseSessionHandler() {
    clusterCommunicator.removeSubscriber(context.closeSessionSubject);
  }

  @Override
  public void registerKeepAliveHandler(Function<KeepAliveRequest, CompletableFuture<KeepAliveResponse>> handler) {
    clusterCommunicator.addSubscriber(context.keepAliveSubject, serializer::decode, handler, serializer::encode);
  }

  @Override
  public void unregisterKeepAliveHandler() {
    clusterCommunicator.removeSubscriber(context.keepAliveSubject);
  }

  @Override
  public void registerQueryHandler(Function<QueryRequest, CompletableFuture<QueryResponse>> handler) {
    clusterCommunicator.addSubscriber(context.querySubject, serializer::decode, handler, serializer::encode);
  }

  @Override
  public void unregisterQueryHandler() {
    clusterCommunicator.removeSubscriber(context.querySubject);
  }

  @Override
  public void registerCommandHandler(Function<CommandRequest, CompletableFuture<CommandResponse>> handler) {
    clusterCommunicator.addSubscriber(context.commandSubject, serializer::decode, handler, serializer::encode);
  }

  @Override
  public void unregisterCommandHandler() {
    clusterCommunicator.removeSubscriber(context.commandSubject);
  }

  @Override
  public void registerMetadataHandler(Function<MetadataRequest, CompletableFuture<MetadataResponse>> handler) {
    clusterCommunicator.addSubscriber(context.metadataSubject, serializer::decode, handler, serializer::encode);
  }

  @Override
  public void unregisterMetadataHandler() {
    clusterCommunicator.removeSubscriber(context.metadataSubject);
  }

  @Override
  public void registerJoinHandler(Function<JoinRequest, CompletableFuture<JoinResponse>> handler) {
    clusterCommunicator.addSubscriber(context.joinSubject, serializer::decode, handler, serializer::encode);
  }

  @Override
  public void unregisterJoinHandler() {
    clusterCommunicator.removeSubscriber(context.joinSubject);
  }

  @Override
  public void registerLeaveHandler(Function<LeaveRequest, CompletableFuture<LeaveResponse>> handler) {
    clusterCommunicator.addSubscriber(context.leaveSubject, serializer::decode, handler, serializer::encode);
  }

  @Override
  public void unregisterLeaveHandler() {
    clusterCommunicator.removeSubscriber(context.leaveSubject);
  }

  @Override
  public void registerConfigureHandler(Function<ConfigureRequest, CompletableFuture<ConfigureResponse>> handler) {
    clusterCommunicator.addSubscriber(context.configureSubject, serializer::decode, handler, serializer::encode);
  }

  @Override
  public void unregisterConfigureHandler() {
    clusterCommunicator.removeSubscriber(context.configureSubject);
  }

  @Override
  public void registerReconfigureHandler(Function<ReconfigureRequest, CompletableFuture<ReconfigureResponse>> handler) {
    clusterCommunicator.addSubscriber(context.reconfigureSubject, serializer::decode, handler, serializer::encode);
  }

  @Override
  public void unregisterReconfigureHandler() {
    clusterCommunicator.removeSubscriber(context.reconfigureSubject);
  }

  @Override
  public void registerInstallHandler(Function<InstallRequest, CompletableFuture<InstallResponse>> handler) {
    clusterCommunicator.addSubscriber(context.installSubject, serializer::decode, handler, serializer::encode);
  }

  @Override
  public void unregisterInstallHandler() {
    clusterCommunicator.removeSubscriber(context.installSubject);
  }

  @Override
  public void registerTransferHandler(Function<TransferRequest, CompletableFuture<TransferResponse>> handler) {
    clusterCommunicator.addSubscriber(context.transferSubject, serializer::decode, handler, serializer::encode);
  }

  @Override
  public void unregisterTransferHandler() {
    clusterCommunicator.removeSubscriber(context.transferSubject);
  }

  @Override
  public void registerPollHandler(Function<PollRequest, CompletableFuture<PollResponse>> handler) {
    clusterCommunicator.addSubscriber(context.pollSubject, serializer::decode, handler, serializer::encode);
  }

  @Override
  public void unregisterPollHandler() {
    clusterCommunicator.removeSubscriber(context.pollSubject);
  }

  @Override
  public void registerVoteHandler(Function<VoteRequest, CompletableFuture<VoteResponse>> handler) {
    clusterCommunicator.addSubscriber(context.voteSubject, serializer::decode, handler, serializer::encode);
  }

  @Override
  public void unregisterVoteHandler() {
    clusterCommunicator.removeSubscriber(context.voteSubject);
  }

  @Override
  public void registerAppendHandler(Function<AppendRequest, CompletableFuture<AppendResponse>> handler) {
    clusterCommunicator.addSubscriber(context.appendSubject, serializer::decode, handler, serializer::encode);
  }

  @Override
  public void unregisterAppendHandler() {
    clusterCommunicator.removeSubscriber(context.appendSubject);
  }

  @Override
  public void registerResetListener(SessionId sessionId, Consumer<ResetRequest> listener, Executor executor) {
    clusterCommunicator.addSubscriber(context.resetSubject(sessionId.id()), serializer::decode, listener, executor);
  }

  @Override
  public void unregisterResetListener(SessionId sessionId) {
    clusterCommunicator.removeSubscriber(context.resetSubject(sessionId.id()));
  }
}
